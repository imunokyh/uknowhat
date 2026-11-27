package kr.or.uknowhat.api.ubusiness.question.service.impl;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.or.uknowhat.api.framework.exception.BizException;
import kr.or.uknowhat.api.ubusiness.common.ErrorCode;
import kr.or.uknowhat.api.ubusiness.common.GamePhase;
import kr.or.uknowhat.api.ubusiness.common.GameSessionRegistry;
import kr.or.uknowhat.api.ubusiness.common.GameSessionRegistry.Role;
import kr.or.uknowhat.api.ubusiness.common.GameSessionRegistry.SessionInfo;
import kr.or.uknowhat.api.ubusiness.common.MessageType;
import kr.or.uknowhat.api.ubusiness.question.domain.Room;
import kr.or.uknowhat.api.ubusiness.question.repositories.RoomRepository;
import kr.or.uknowhat.api.ubusiness.question.service.GameService;
import kr.or.uknowhat.api.ubusiness.question.service.RoomQuestionService;
import kr.or.uknowhat.api.ubusiness.question.vo.GameQuestion;
import kr.or.uknowhat.api.ubusiness.question.vo.MessageVo;
import kr.or.uknowhat.api.ubusiness.question.vo.RoomQuestionResMapping;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GameServiceImpl implements GameService {

	private static final long COUNTDOWN_MS = 5000L;
	private static final long TICK_MS = 1000L;
	private static final long KEY_TTL_HOURS = 24L;
	private static final int MAX_NAME_LENGTH = 40;
	private static final Pattern ROOM_NUMBER_PATTERN = Pattern.compile("^\\d{4}$");

	// Redis key suffix (방 번호 + suffix). 방 번호 자체는 참여자 점수 ZSet 키.
	private static final String K_GAME = "_game";          // hash: 진행 상태
	private static final String K_PROBS = "_probs";        // value: 문제 목록 JSON
	private static final String K_ANSWERS = "_answers";    // hash: 참여자 → 제출 답
	private static final String K_ELAPSED = "_elapsed_ts"; // hash: 참여자 → 제출 소요시간(ms)
	private static final String K_RESULTS = "_results";    // hash: 참여자 → 현재 문제 채점 결과 ("Y:점수" / "N:0")
	private static final String K_TOKENS = "_tokens";      // hash: 참여자 → 재접속 토큰
	private static final String K_ANSLIST = "_anslist";    // value: 정답자 목록 JSON
	// 이전 버전에서 사용하던 키 (정리용)
	private static final String K_LEGACY_PROBLEM = "_problem";
	private static final String K_LEGACY_START_TS = "_start_ts";

	private static final String F_PHASE = "phase";
	private static final String F_INDEX = "idx";
	private static final String F_DEADLINE = "deadline";
	private static final String F_QSTART = "qstart";
	private static final String F_OWNER = "owner";

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Autowired
	private RoomQuestionService roomQuestionService;

	@Autowired
	private RoomRepository roomRepository;

	@Autowired
	private GameSessionRegistry sessionRegistry;

	private final ObjectMapper objectMapper = new ObjectMapper()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	private final ConcurrentHashMap<String, ScheduledFuture<?>> tickers = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, Object> locks = new ConcurrentHashMap<>();
	private ThreadPoolTaskScheduler scheduler;

	@PostConstruct
	public void init() {
		scheduler = new ThreadPoolTaskScheduler();
		scheduler.setPoolSize(4);
		scheduler.setThreadNamePrefix("game-timer-");
		scheduler.setErrorHandler(t -> log.error("게임 타이머 작업 중 오류", t));
		scheduler.initialize();
	}

	@PreDestroy
	public void destroy() {
		scheduler.shutdown();
	}

	/**
	 * 서버 재시작 시 Redis 에 남아있는 진행 중 게임의 타이머를 복구한다.
	 */
	@EventListener(ApplicationReadyEvent.class)
	public void recoverRunningGames() {
		try {
			Set<String> keys = redisTemplate.keys("*" + K_GAME);
			if (keys == null) return;
			for (String key : keys) {
				String roomNumber = key.substring(0, key.length() - K_GAME.length());
				if (ROOM_NUMBER_PATTERN.matcher(roomNumber).matches()) {
					ensureTicker(roomNumber);
				}
			}
		} catch (Exception e) {
			log.warn("진행 중 게임 타이머 복구 실패: {}", e.getMessage());
		}
	}

	/**
	 * 연결이 끊긴 참여자를 다른 사람들에게 알린다. 게임 진행은 그대로 유지된다.
	 */
	@EventListener
	public void onSessionDisconnect(SessionDisconnectEvent event) {
		SessionInfo info = sessionRegistry.unregister(event.getSessionId());
		if (info == null) return;

		if (info.getRole() == Role.EXAMINER) {
			log.info("[{}] 출제자 연결 끊김 - 게임은 계속 진행됩니다.", info.getRoomNumber());
			return;
		}

		if (!sessionRegistry.isOnline(info.getRoomNumber(), info.getName())) {
			log.info("[{}] 참여자 연결 끊김: {}", info.getRoomNumber(), info.getName());
			MessageVo msg = message(info.getRoomNumber(), MessageType.OFFLINE);
			msg.setParticipantName(info.getName());
			msg.setContent(info.getName() + "님의 연결이 끊어졌습니다.");
			broadcast(info.getRoomNumber(), msg);
		}
	}

	// ------------------------------------------------------------------
	// 참여자 입장 / 퇴장
	// ------------------------------------------------------------------

	@Override
	public boolean checkNickname(String roomNumber, String name, String token) {
		validateRoomNumber(roomNumber);
		validateName(name);

		if (redisTemplate.opsForZSet().score(roomNumber, name) == null)
			return false;

		String savedToken = hashGet(roomNumber + K_TOKENS, name);
		if (StringUtils.hasText(token) && Objects.equals(savedToken, token))
			return true;

		throw new BizException(ErrorCode.DUPLICATE_NICKNAME, ErrorCode.DUPLICATE_NICKNAME.getMessage());
	}

	@Override
	public boolean join(String roomNumber, String name, String token, String sessionId) {
		validateRoomNumber(roomNumber);
		validateName(name);
		assertRoomPlaying(roomNumber);

		boolean rejoin = withLock(roomNumber, () -> {
			Double score = redisTemplate.opsForZSet().score(roomNumber, name);
			String savedToken = hashGet(roomNumber + K_TOKENS, name);

			if (score != null) {
				// 이미 존재하는 닉네임: 토큰이 일치해야 재접속 허용 (닉네임 도용 방지)
				if (savedToken != null && !Objects.equals(savedToken, token))
					throw new BizException(ErrorCode.DUPLICATE_NICKNAME, ErrorCode.DUPLICATE_NICKNAME.getMessage());
				if (savedToken == null && StringUtils.hasText(token))
					redisTemplate.opsForHash().put(roomNumber + K_TOKENS, name, token);
				return true;
			}

			// 신규 참여자: 기존 점수를 덮어쓰지 않도록 존재하지 않을 때만 추가
			redisTemplate.opsForZSet().addIfAbsent(roomNumber, name, 0);
			if (StringUtils.hasText(token))
				redisTemplate.opsForHash().put(roomNumber + K_TOKENS, name, token);
			touch(roomNumber);
			return false;
		});

		sessionRegistry.register(sessionId, roomNumber, name, Role.PARTICIPANT);
		return rejoin;
	}

	@Override
	public void leave(String roomNumber, String name, String token, String sessionId) {
		validateRoomNumber(roomNumber);
		validateName(name);
		// 퇴장 직후 소켓이 끊기면서 세션이 먼저 해제될 수 있으므로 세션 대신 토큰으로 본인 확인
		String savedToken = hashGet(roomNumber + K_TOKENS, name);
		boolean ownSession = sessionRegistry.isSessionOf(sessionId, roomNumber, name);
		if (savedToken != null ? !Objects.equals(savedToken, token) : !ownSession)
			throw new BizException(ErrorCode.INVALID_SESSION, ErrorCode.INVALID_SESSION.getMessage());

		if (ownSession)
			sessionRegistry.unregister(sessionId);

		MessageVo msg = message(roomNumber, MessageType.UNJOIN);
		msg.setParticipantName(name);

		withLock(roomNumber, () -> {
			if (getPhase(roomNumber) == GamePhase.WAITING) {
				// 시작 전 퇴장: 목록에서 완전히 제거
				redisTemplate.opsForZSet().remove(roomNumber, name);
				redisTemplate.opsForHash().delete(roomNumber + K_TOKENS, name);
				msg.setContent(name + "님이 퇴장하셨습니다.");
			} else {
				// 진행 중 퇴장: 순위 일관성을 위해 점수는 유지하고 오프라인으로만 표시
				msg.setType(MessageType.OFFLINE);
				msg.setContent(name + "님이 게임을 나갔습니다.");
			}
			broadcast(roomNumber, msg);
		});
	}

	@Override
	public void registerExaminer(String roomNumber, Principal principal, String sessionId) {
		assertOwner(roomNumber, principal);
		sessionRegistry.register(sessionId, roomNumber, principal.getName(), Role.EXAMINER);
	}

	// ------------------------------------------------------------------
	// 출제자 진행 제어
	// ------------------------------------------------------------------

	@Override
	public void start(String roomNumber, Principal principal) {
		assertOwner(roomNumber, principal);

		withLock(roomNumber, () -> {
			if (getPhase(roomNumber) != GamePhase.WAITING)
				throw new BizException(ErrorCode.INVALID_STATE, "이미 진행 중인 게임입니다.");

			Long participantCount = redisTemplate.opsForZSet().zCard(roomNumber);
			if (participantCount == null || participantCount == 0)
				throw new BizException(ErrorCode.INVALID_STATE, "아직 참가자가 없습니다.");

			List<RoomQuestionResMapping> rqList = roomQuestionService.readQuestion(roomNumber);
			List<GameQuestion> questions = new ArrayList<>();
			if (rqList != null) {
				for (RoomQuestionResMapping rq : rqList) {
					GameQuestion q = GameQuestion.of(rq);
					if (q.isSupportedType()) {
						questions.add(q);
					} else {
						log.warn("[{}] 지원하지 않는 문제 유형이라 제외합니다. id={}, type={}", roomNumber, q.getId(), q.getQuestionType());
					}
				}
			}
			if (questions.isEmpty())
				throw new BizException(ErrorCode.INVALID_STATE, "진행할 수 있는 문제가 없습니다. 문제를 등록해 주세요.");

			redisTemplate.opsForValue().set(roomNumber + K_PROBS, toJson(questions));
			touch(roomNumber);

			broadcast(roomNumber, message(roomNumber, MessageType.START));

			// 정답이 포함된 문제 목록은 출제자에게만 전송
			MessageVo probMsg = message(roomNumber, MessageType.READPROB);
			probMsg.setResult(questions);
			messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/game", probMsg);

			startCountdown(roomNumber, 0);
			log.info("[{}] 게임 시작 - 문제 {}개, 참여자 {}명", roomNumber, questions.size(), participantCount);
		});
	}

	@Override
	public void next(String roomNumber, Principal principal, String expectedPhase) {
		assertOwner(roomNumber, principal);

		withLock(roomNumber, () -> {
			GamePhase phase = getPhase(roomNumber);

			if (StringUtils.hasText(expectedPhase) && !phase.name().equals(expectedPhase)) {
				// 중복 클릭 / 재접속 직후의 오래된 요청: 진행하지 않고 클라이언트가 상태를 다시 맞추도록 알린다.
				log.info("[{}] NEXT 무시 - 요청 단계 {}, 현재 단계 {}", roomNumber, expectedPhase, phase);
				throw new BizException(ErrorCode.INVALID_STATE, "이미 다음 단계로 진행되었습니다. 화면을 현재 상태로 갱신합니다.");
			}

			switch (phase) {
			case QUESTION:
				closeQuestionInternal(roomNumber);
				break;
			case RESULT:
				if (getIndex(roomNumber) >= loadQuestions(roomNumber).size() - 1) {
					finishInternal(roomNumber);
				} else {
					hashPut(roomNumber + K_GAME, F_PHASE, GamePhase.RANK.name());
					MessageVo rankMsg = message(roomNumber, MessageType.RANK);
					rankMsg.setResult(buildRankList(roomNumber));
					broadcast(roomNumber, rankMsg);
				}
				break;
			case RANK:
				startCountdown(roomNumber, getIndex(roomNumber) + 1);
				break;
			case WAITING:
				throw new BizException(ErrorCode.INVALID_STATE, "아직 게임이 시작되지 않았습니다.");
			case COUNTDOWN:
				throw new BizException(ErrorCode.INVALID_STATE, "문제를 준비 중입니다. 잠시 후 다시 시도해 주세요.");
			case FINISHED:
			default:
				throw new BizException(ErrorCode.INVALID_STATE, "이미 종료된 게임입니다.");
			}
		});
	}

	@Override
	public void closeQuestion(String roomNumber, Principal principal) {
		assertOwner(roomNumber, principal);
		withLock(roomNumber, () -> {
			if (getPhase(roomNumber) == GamePhase.QUESTION)
				closeQuestionInternal(roomNumber);
		});
	}

	// ------------------------------------------------------------------
	// 참여자 답안 제출
	// ------------------------------------------------------------------

	@Override
	public void submitAnswer(String roomNumber, String name, String sessionId, Integer questionIndex, String answer) {
		validateRoomNumber(roomNumber);
		if (!sessionRegistry.isSessionOf(sessionId, roomNumber, name))
			throw new BizException(ErrorCode.INVALID_SESSION, "참여 정보가 확인되지 않았습니다. 재접속 후 다시 시도해 주세요.");

		withLock(roomNumber, () -> {
			if (getPhase(roomNumber) != GamePhase.QUESTION)
				throw new BizException(ErrorCode.INVALID_STATE, "답안 제출 시간이 아닙니다.");

			int index = getIndex(roomNumber);
			if (questionIndex != null && questionIndex != index)
				throw new BizException(ErrorCode.INVALID_STATE, "이미 지난 문제입니다.");

			long deadline = getLong(roomNumber, F_DEADLINE, 0L);
			long now = System.currentTimeMillis();
			if (deadline > 0 && now > deadline)
				throw new BizException(ErrorCode.INVALID_STATE, "제한 시간이 지났습니다.");

			GameQuestion q = loadQuestions(roomNumber).get(index);
			int choice;
			try {
				choice = Integer.parseInt(String.valueOf(answer).trim());
			} catch (NumberFormatException e) {
				throw new BizException(ErrorCode.WRONG_REQUEST, "올바르지 않은 답안입니다.");
			}
			if (choice < 1 || choice > q.choiceCount())
				throw new BizException(ErrorCode.WRONG_REQUEST, "올바르지 않은 답안입니다.");

			Boolean first = redisTemplate.opsForHash().putIfAbsent(roomNumber + K_ANSWERS, name, String.valueOf(choice));
			if (!Boolean.TRUE.equals(first))
				throw new BizException(ErrorCode.INVALID_STATE, "이미 답안을 제출했습니다.");

			long qStart = getLong(roomNumber, F_QSTART, now);
			redisTemplate.opsForHash().put(roomNumber + K_ELAPSED, name, String.valueOf(Math.max(0, now - qStart)));

			Long answerCount = redisTemplate.opsForHash().size(roomNumber + K_ANSWERS);

			// 출제자에게는 제출 수만 전달 (다른 참여자의 답은 노출하지 않음)
			MessageVo countMsg = message(roomNumber, MessageType.ANSWER);
			countMsg.setQuestionIndex(index);
			countMsg.setContent(String.valueOf(answerCount));
			broadcast(roomNumber, countMsg);

			// 접속 중인 참여자가 모두 제출하면 조기 종료
			Set<String> online = sessionRegistry.onlineParticipants(roomNumber);
			Map<Object, Object> answers = redisTemplate.opsForHash().entries(roomNumber + K_ANSWERS);
			if (!online.isEmpty() && online.stream().allMatch(answers::containsKey)) {
				log.info("[{}] 접속 중인 참여자 전원 제출 - 문제 조기 종료", roomNumber);
				closeQuestionInternal(roomNumber);
			}
		});
	}

	// ------------------------------------------------------------------
	// 상태 조회 (재접속 시 복원용)
	// ------------------------------------------------------------------

	@Override
	public Map<String, Object> examinerState(String roomNumber, Principal principal) {
		assertOwner(roomNumber, principal);
		ensureTicker(roomNumber);

		return withLock(roomNumber, () -> {
			GamePhase phase = getPhase(roomNumber);
			List<GameQuestion> questions = phase == GamePhase.WAITING ? Collections.emptyList() : loadQuestions(roomNumber);
			int index = getIndex(roomNumber);

			Map<String, Object> state = baseState(roomNumber, phase, index, questions.size());
			state.put("questions", questions);
			state.put("answerCount", redisTemplate.opsForHash().size(roomNumber + K_ANSWERS));

			if (phase == GamePhase.RESULT || phase == GamePhase.RANK) {
				state.put("answerChart", buildAnswerChart(roomNumber));
				state.put("answerUserList", fromJson(redisTemplate.opsForValue().get(roomNumber + K_ANSLIST),
						new TypeReference<List<Map<String, String>>>() {}, new ArrayList<>()));
			}
			if (phase == GamePhase.RANK || phase == GamePhase.FINISHED) {
				state.put("rankList", buildRankList(roomNumber));
			}

			Set<String> online = sessionRegistry.onlineParticipants(roomNumber);
			List<Map<String, Object>> participants = new ArrayList<>();
			for (String name : participantNames(roomNumber)) {
				Map<String, Object> p = new LinkedHashMap<>();
				p.put("name", name);
				p.put("online", online.contains(name));
				participants.add(p);
			}
			state.put("participants", participants);
			return state;
		});
	}

	@Override
	public Map<String, Object> participantState(String roomNumber, String name, String token) {
		validateRoomNumber(roomNumber);
		validateName(name);
		assertRoomPlaying(roomNumber);

		if (redisTemplate.opsForZSet().score(roomNumber, name) == null)
			throw new BizException(ErrorCode.INVALID_SESSION, "참여 정보가 없습니다. 다시 입장해 주세요.");
		String savedToken = hashGet(roomNumber + K_TOKENS, name);
		if (savedToken != null && !Objects.equals(savedToken, token))
			throw new BizException(ErrorCode.INVALID_SESSION, ErrorCode.INVALID_SESSION.getMessage());

		ensureTicker(roomNumber);

		return withLock(roomNumber, () -> {
			GamePhase phase = getPhase(roomNumber);
			List<GameQuestion> questions = phase == GamePhase.WAITING ? Collections.emptyList() : loadQuestions(roomNumber);
			int index = getIndex(roomNumber);

			Map<String, Object> state = baseState(roomNumber, phase, index, questions.size());
			state.put("participants", participantNames(roomNumber));

			Double total = redisTemplate.opsForZSet().score(roomNumber, name);
			state.put("totalScore", total == null ? 0L : total.longValue());

			if (phase == GamePhase.QUESTION && index < questions.size()) {
				GameQuestion q = questions.get(index);
				state.put("questionType", q.getQuestionType());
				state.put("answer1Text", q.getAnswer1Text());
				state.put("answer2Text", q.getAnswer2Text());
				state.put("answer3Text", q.getAnswer3Text());
				state.put("answer4Text", q.getAnswer4Text());
				state.put("answered", redisTemplate.opsForHash().hasKey(roomNumber + K_ANSWERS, name));
			}

			if (phase == GamePhase.RESULT || phase == GamePhase.RANK) {
				String result = hashGet(roomNumber + K_RESULTS, name);
				if (result == null) {
					state.put("result", "TIMEOUT");
				} else {
					String[] parts = result.split(":");
					state.put("result", "Y".equals(parts[0]) ? "CORRECT" : "INCORRECT");
					state.put("score", parts.length > 1 ? Long.parseLong(parts[1]) : 0L);
				}
			}

			if (phase == GamePhase.FINISHED) {
				for (Map<String, String> rank : buildRankList(roomNumber)) {
					if (name.equals(rank.get("value"))) {
						state.put("finalRank", Integer.parseInt(rank.get("rank")));
						break;
					}
				}
			}
			return state;
		});
	}

	// ------------------------------------------------------------------
	// 방 정리
	// ------------------------------------------------------------------

	@Override
	public void closeRoom(String roomNumber) {
		if (!StringUtils.hasText(roomNumber)) return;
		withLock(roomNumber, () -> {
			stopTicker(roomNumber);
			MessageVo exitMsg = message(roomNumber, MessageType.EXIT);
			exitMsg.setContent("방이 종료되었습니다.");
			broadcast(roomNumber, exitMsg);
			deleteKeys(roomNumber);
		});
		log.info("[{}] 방 종료 - 게임 데이터 삭제", roomNumber);
	}

	@Override
	public void resetRoom(String roomNumber) {
		if (!StringUtils.hasText(roomNumber)) return;
		withLock(roomNumber, () -> {
			stopTicker(roomNumber);
			deleteKeys(roomNumber);
		});
	}

	@Override
	public void assertOwner(String roomNumber, Principal principal) {
		validateRoomNumber(roomNumber);
		if (principal == null || !StringUtils.hasText(principal.getName()))
			throw new BizException(ErrorCode.UNAUTHORIZED, ErrorCode.UNAUTHORIZED.getMessage());

		Room room = assertRoomPlaying(roomNumber);
		if (!Objects.equals(room.getCreatedUserId(), principal.getName()))
			throw new BizException(ErrorCode.FORBIDDEN, "해당 방의 출제자만 진행할 수 있습니다.");
	}

	// ------------------------------------------------------------------
	// 내부 진행 로직 (반드시 room lock 안에서 호출)
	// ------------------------------------------------------------------

	private void startCountdown(String roomNumber, int index) {
		List<GameQuestion> questions = loadQuestions(roomNumber);
		if (index >= questions.size()) {
			finishInternal(roomNumber);
			return;
		}

		Map<String, String> fields = new HashMap<>();
		fields.put(F_PHASE, GamePhase.COUNTDOWN.name());
		fields.put(F_INDEX, String.valueOf(index));
		fields.put(F_DEADLINE, String.valueOf(System.currentTimeMillis() + COUNTDOWN_MS));
		redisTemplate.opsForHash().putAll(roomNumber + K_GAME, fields);
		touch(roomNumber);

		MessageVo msg = message(roomNumber, MessageType.LOADING);
		msg.setQuestionIndex(index);
		msg.setContent(String.valueOf(COUNTDOWN_MS / 1000));
		broadcast(roomNumber, msg);

		startTicker(roomNumber);
	}

	private void openQuestion(String roomNumber, int index) {
		List<GameQuestion> questions = loadQuestions(roomNumber);
		if (index >= questions.size()) {
			finishInternal(roomNumber);
			return;
		}
		GameQuestion q = questions.get(index);

		redisTemplate.delete(Arrays.asList(roomNumber + K_ANSWERS, roomNumber + K_ELAPSED,
				roomNumber + K_RESULTS, roomNumber + K_ANSLIST));

		long now = System.currentTimeMillis();
		long timeSec = q.getQuestionTime() == null ? 0L : q.getQuestionTime();

		Map<String, String> fields = new HashMap<>();
		fields.put(F_PHASE, GamePhase.QUESTION.name());
		fields.put(F_INDEX, String.valueOf(index));
		fields.put(F_QSTART, String.valueOf(now));
		fields.put(F_DEADLINE, String.valueOf(timeSec > 0 ? now + timeSec * 1000 : 0));
		redisTemplate.opsForHash().putAll(roomNumber + K_GAME, fields);

		MessageVo msg = message(roomNumber, GameQuestion.TYPE_OX.equals(q.getQuestionType()) ? MessageType.OXP : MessageType.OBP);
		msg.setQuestionIndex(index);
		msg.setContent(q.getQuestionText());
		msg.setAnswer1Text(q.getAnswer1Text());
		msg.setAnswer2Text(q.getAnswer2Text());
		msg.setAnswer3Text(q.getAnswer3Text());
		msg.setAnswer4Text(q.getAnswer4Text());
		broadcast(roomNumber, msg);

		if (timeSec > 0) {
			MessageVo timeMsg = message(roomNumber, MessageType.TIMECNT);
			timeMsg.setQuestionIndex(index);
			timeMsg.setContent(String.valueOf(timeSec));
			broadcast(roomNumber, timeMsg);
			startTicker(roomNumber);
		} else {
			// 제한 시간 없는 문제: 출제자가 종료하거나 전원이 제출할 때까지 대기
			stopTicker(roomNumber);
		}
	}

	private void closeQuestionInternal(String roomNumber) {
		if (getPhase(roomNumber) != GamePhase.QUESTION) return;
		stopTicker(roomNumber);

		int index = getIndex(roomNumber);
		GameQuestion q = loadQuestions(roomNumber).get(index);
		hashPut(roomNumber + K_GAME, F_PHASE, GamePhase.RESULT.name());

		MessageVo timeoutMsg = message(roomNumber, MessageType.TIMEOUT);
		timeoutMsg.setQuestionIndex(index);
		broadcast(roomNumber, timeoutMsg);

		Map<Object, Object> answers = redisTemplate.opsForHash().entries(roomNumber + K_ANSWERS);
		Map<Object, Object> elapsedMap = redisTemplate.opsForHash().entries(roomNumber + K_ELAPSED);

		String questionAnswer = String.valueOf(q.getQuestionAnswer()).trim();
		long questionScore = q.getQuestionScore() == null ? 0L : q.getQuestionScore();
		long questionTime = (q.getQuestionTime() == null ? 0L : q.getQuestionTime()) * 1000;

		List<Map<String, String>> answerUserList = new ArrayList<>();
		List<Long> answerUserElapsed = new ArrayList<>();
		Map<String, String> results = new HashMap<>();

		for (Map.Entry<Object, Object> entry : answers.entrySet()) {
			String name = String.valueOf(entry.getKey());
			try {
				MessageVo personal = message(roomNumber, MessageType.INCORRECT);
				personal.setQuestionIndex(index);

				if (questionAnswer.equals(String.valueOf(entry.getValue()))) {
					long elapsed = parseLong(elapsedMap.get(entry.getKey()), questionTime);
					long realScore;
					if (questionTime == 0) {
						// 제한 시간 없는 문제는 무조건 문제의 점수로 부여
						realScore = questionScore;
					} else {
						// 점수 산출 방식 = 기본 점수 (문제 점수 / 2) + 제출 시간에 따른 추가 점수 (나머지 점수 * 문제 시간 대비, 남은 시간 비율)
						long remain = Math.max(0, questionTime - elapsed);
						realScore = (long) ((questionScore / 2.0) * ((double) remain / (double) questionTime)) + (long) (questionScore / 2.0);
					}

					Double total = redisTemplate.opsForZSet().incrementScore(roomNumber, name, realScore);
					results.put(name, "Y:" + realScore);

					personal.setType(MessageType.CORRECT);
					personal.setScore(realScore);
					Map<String, Object> extra = new HashMap<>();
					extra.put("totalScore", total == null ? realScore : total.longValue());
					personal.setResult(extra);

					Map<String, String> user = new HashMap<>();
					user.put("value", name);
					user.put("time", ((double) elapsed / 1000.0) + "s");
					answerUserList.add(user);
					answerUserElapsed.add(elapsed);
				} else {
					results.put(name, "N:0");
				}

				messagingTemplate.convertAndSend(personalTopic(roomNumber, name), personal);
			} catch (Exception e) {
				// 한 참여자 처리 실패가 전체 채점을 막지 않도록 한다.
				log.error("[{}] 채점 중 오류 - 참여자 {}", roomNumber, name, e);
			}
		}

		// 정답자 목록은 제출이 빠른 순으로 정렬
		List<Integer> order = new ArrayList<>();
		for (int i = 0; i < answerUserList.size(); i++) order.add(i);
		order.sort(Comparator.comparing(answerUserElapsed::get));
		List<Map<String, String>> sortedUsers = order.stream().map(answerUserList::get).collect(Collectors.toList());

		if (!results.isEmpty())
			redisTemplate.opsForHash().putAll(roomNumber + K_RESULTS, results);
		redisTemplate.opsForValue().set(roomNumber + K_ANSLIST, toJson(sortedUsers));
		touch(roomNumber);

		MessageVo userListMsg = message(roomNumber, MessageType.ANSUSERLIST);
		userListMsg.setQuestionIndex(index);
		userListMsg.setResult(sortedUsers);
		broadcast(roomNumber, userListMsg);

		MessageVo chartMsg = message(roomNumber, MessageType.ANSCHART);
		chartMsg.setQuestionIndex(index);
		chartMsg.setResult(buildAnswerChart(roomNumber));
		broadcast(roomNumber, chartMsg);

		log.info("[{}] {}번 문제 채점 완료 - 제출 {}명, 정답 {}명", roomNumber, index + 1, answers.size(), sortedUsers.size());
	}

	private void finishInternal(String roomNumber) {
		stopTicker(roomNumber);
		hashPut(roomNumber + K_GAME, F_PHASE, GamePhase.FINISHED.name());

		List<Map<String, String>> rankList = buildRankList(roomNumber);

		MessageVo rankMsg = message(roomNumber, MessageType.RANK);
		rankMsg.setResult(rankList);
		broadcast(roomNumber, rankMsg);

		for (Map<String, String> rank : rankList) {
			MessageVo finalMsg = message(roomNumber, MessageType.FINALRANK);
			finalMsg.setContent(rank.get("rank"));
			messagingTemplate.convertAndSend(personalTopic(roomNumber, rank.get("value")), finalMsg);
		}

		broadcast(roomNumber, message(roomNumber, MessageType.FINISH));
		log.info("[{}] 게임 종료 - 최종 순위 전송", roomNumber);
	}

	// ------------------------------------------------------------------
	// 타이머
	// ------------------------------------------------------------------

	private void startTicker(String roomNumber) {
		stopTicker(roomNumber);
		ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(() -> tick(roomNumber),
				new Date(System.currentTimeMillis() + TICK_MS), TICK_MS);
		tickers.put(roomNumber, future);
	}

	private void stopTicker(String roomNumber) {
		ScheduledFuture<?> future = tickers.remove(roomNumber);
		if (future != null)
			future.cancel(false);
	}

	/** 타이머가 필요한 단계인데 타이머가 없으면(서버 재시작 등) 다시 시작한다. */
	private void ensureTicker(String roomNumber) {
		try {
			withLock(roomNumber, () -> {
				ScheduledFuture<?> future = tickers.get(roomNumber);
				if (future != null && !future.isDone()) return;

				GamePhase phase = getPhase(roomNumber);
				long deadline = getLong(roomNumber, F_DEADLINE, 0L);
				if (phase == GamePhase.COUNTDOWN || (phase == GamePhase.QUESTION && deadline > 0)) {
					log.info("[{}] {} 단계 타이머 복구", roomNumber, phase);
					startTicker(roomNumber);
				}
			});
		} catch (Exception e) {
			log.warn("[{}] 타이머 복구 실패: {}", roomNumber, e.getMessage());
		}
	}

	private void tick(String roomNumber) {
		try {
			withLock(roomNumber, () -> {
				GamePhase phase = getPhase(roomNumber);
				long deadline = getLong(roomNumber, F_DEADLINE, 0L);
				long remainMs = deadline - System.currentTimeMillis();
				long remainSec = Math.max(0, Math.round(remainMs / 1000.0));

				if (phase == GamePhase.COUNTDOWN) {
					if (remainSec <= 0) {
						openQuestion(roomNumber, getIndex(roomNumber));
					} else {
						MessageVo msg = message(roomNumber, MessageType.COUNTDOWN);
						msg.setQuestionIndex(getIndex(roomNumber));
						msg.setContent(String.valueOf(remainSec));
						broadcast(roomNumber, msg);
					}
				} else if (phase == GamePhase.QUESTION && deadline > 0) {
					MessageVo msg = message(roomNumber, MessageType.TIMECNT);
					msg.setQuestionIndex(getIndex(roomNumber));
					msg.setContent(String.valueOf(remainSec));
					broadcast(roomNumber, msg);
					if (remainSec <= 0)
						closeQuestionInternal(roomNumber);
				} else {
					stopTicker(roomNumber);
				}
			});
		} catch (Exception e) {
			log.error("[{}] 타이머 처리 중 오류", roomNumber, e);
		}
	}

	// ------------------------------------------------------------------
	// helpers
	// ------------------------------------------------------------------

	private Map<String, Object> baseState(String roomNumber, GamePhase phase, int index, int count) {
		Map<String, Object> state = new LinkedHashMap<>();
		state.put("roomNumber", roomNumber);
		state.put("phase", phase.name());
		state.put("questionIndex", index);
		state.put("questionCount", count);

		long deadline = getLong(roomNumber, F_DEADLINE, 0L);
		if ((phase == GamePhase.COUNTDOWN || phase == GamePhase.QUESTION) && deadline > 0) {
			state.put("remainSec", Math.max(0, Math.round((deadline - System.currentTimeMillis()) / 1000.0)));
		} else {
			state.put("remainSec", 0);
		}
		return state;
	}

	private List<Map<String, String>> buildRankList(String roomNumber) {
		Set<TypedTuple<Object>> rankSet = redisTemplate.opsForZSet().reverseRangeWithScores(roomNumber, 0, -1);
		List<Map<String, String>> rankList = new ArrayList<>();
		if (rankSet == null) return rankList;

		Double prevScore = null;
		int rank = 1, accumulateRank = 1;
		for (TypedTuple<Object> tuple : rankSet) {
			double curScore = tuple.getScore() == null ? 0 : tuple.getScore();
			if (prevScore == null || Double.compare(prevScore, curScore) != 0)
				rank = accumulateRank;

			Map<String, String> item = new LinkedHashMap<>();
			item.put("rank", String.valueOf(rank));
			item.put("value", String.valueOf(tuple.getValue()));
			item.put("score", String.valueOf(curScore));
			rankList.add(item);

			accumulateRank++;
			prevScore = curScore;
		}
		return rankList;
	}

	private Map<String, Integer> buildAnswerChart(String roomNumber) {
		Map<Object, Object> answers = redisTemplate.opsForHash().entries(roomNumber + K_ANSWERS);
		Map<String, Integer> chart = new TreeMap<>();
		for (Object value : answers.values()) {
			chart.merge(String.valueOf(value), 1, Integer::sum);
		}
		return chart;
	}

	private List<String> participantNames(String roomNumber) {
		Set<Object> set = redisTemplate.opsForZSet().range(roomNumber, 0, -1);
		List<String> names = new ArrayList<>();
		if (set != null)
			set.forEach(o -> names.add(String.valueOf(o)));
		return names;
	}

	private List<GameQuestion> loadQuestions(String roomNumber) {
		List<GameQuestion> questions = fromJson(redisTemplate.opsForValue().get(roomNumber + K_PROBS),
				new TypeReference<List<GameQuestion>>() {}, null);
		if (questions == null || questions.isEmpty())
			throw new BizException(ErrorCode.INVALID_STATE, "게임 문제 정보를 찾을 수 없습니다. 방을 종료 후 다시 시작해 주세요.");
		return questions;
	}

	private Room assertRoomPlaying(String roomNumber) {
		Room room = roomRepository.findByRoomNumber(roomNumber)
				.orElseThrow(() -> new BizException(ErrorCode.ROOM_NOT_FOUND, "방을 찾을 수 없거나 이미 종료된 방입니다."));
		if (!"PLAY".equals(room.getRoomState()))
			throw new BizException(ErrorCode.ROOM_NOT_FOUND, "진행 중인 방이 아닙니다.");
		return room;
	}

	private void validateRoomNumber(String roomNumber) {
		if (roomNumber == null || !ROOM_NUMBER_PATTERN.matcher(roomNumber).matches())
			throw new BizException(ErrorCode.WRONG_REQUEST, "방 번호가 올바르지 않습니다.");
	}

	private void validateName(String name) {
		if (!StringUtils.hasText(name))
			throw new BizException(ErrorCode.WRONG_REQUEST, "닉네임을 입력하세요.");
		if (name.length() > MAX_NAME_LENGTH)
			throw new BizException(ErrorCode.WRONG_REQUEST, "닉네임은 " + MAX_NAME_LENGTH + "자 이하로 입력하세요.");
		if (EXAMINER_NAME.equals(name.trim()))
			throw new BizException(ErrorCode.WRONG_REQUEST, "사용할 수 없는 닉네임입니다.");
	}

	private GamePhase getPhase(String roomNumber) {
		String phase = hashGet(roomNumber + K_GAME, F_PHASE);
		if (phase == null) return GamePhase.WAITING;
		try {
			return GamePhase.valueOf(phase);
		} catch (IllegalArgumentException e) {
			log.warn("[{}] 알 수 없는 게임 단계: {}", roomNumber, phase);
			return GamePhase.WAITING;
		}
	}

	private int getIndex(String roomNumber) {
		return (int) getLong(roomNumber, F_INDEX, 0L);
	}

	private long getLong(String roomNumber, String field, long defaultValue) {
		return parseLong(hashGet(roomNumber + K_GAME, field), defaultValue);
	}

	private static long parseLong(Object value, long defaultValue) {
		if (value == null) return defaultValue;
		try {
			return Long.parseLong(String.valueOf(value));
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	private String hashGet(String key, String field) {
		Object value = redisTemplate.opsForHash().get(key, field);
		return value == null ? null : String.valueOf(value);
	}

	private void hashPut(String key, String field, String value) {
		redisTemplate.opsForHash().put(key, field, value);
	}

	private List<String> allKeys(String roomNumber) {
		return Arrays.asList(roomNumber, roomNumber + K_GAME, roomNumber + K_PROBS, roomNumber + K_ANSWERS,
				roomNumber + K_ELAPSED, roomNumber + K_RESULTS, roomNumber + K_TOKENS, roomNumber + K_ANSLIST,
				roomNumber + K_LEGACY_PROBLEM, roomNumber + K_LEGACY_START_TS);
	}

	private void deleteKeys(String roomNumber) {
		redisTemplate.delete(allKeys(roomNumber));
	}

	/** 비정상 종료된 방의 데이터가 Redis 에 영구히 남지 않도록 만료시간을 갱신한다. */
	private void touch(String roomNumber) {
		for (String key : allKeys(roomNumber)) {
			redisTemplate.expire(key, KEY_TTL_HOURS, TimeUnit.HOURS);
		}
	}

	private String toJson(Object value) {
		try {
			return objectMapper.writeValueAsString(value);
		} catch (JsonProcessingException e) {
			throw new BizException("게임 데이터 저장 중 오류가 발생했습니다.", e);
		}
	}

	private <T> T fromJson(Object json, TypeReference<T> type, T defaultValue) {
		if (json == null) return defaultValue;
		try {
			return objectMapper.readValue(String.valueOf(json), type);
		} catch (JsonProcessingException e) {
			log.error("게임 데이터 변환 오류", e);
			return defaultValue;
		}
	}

	private MessageVo message(String roomNumber, MessageType type) {
		MessageVo msg = new MessageVo();
		msg.setRoomNumber(roomNumber);
		msg.setType(type);
		return msg;
	}

	private void broadcast(String roomNumber, MessageVo msg) {
		messagingTemplate.convertAndSend("/subscribe/play/room/" + roomNumber, msg);
	}

	private static String personalTopic(String roomNumber, String name) {
		return "/subscribe/play/room/" + roomNumber + "/" + name;
	}

	private <T> T withLock(String roomNumber, Supplier<T> action) {
		Object lock = locks.computeIfAbsent(roomNumber, k -> new Object());
		synchronized (lock) {
			return action.get();
		}
	}

	private void withLock(String roomNumber, Runnable action) {
		withLock(roomNumber, () -> {
			action.run();
			return null;
		});
	}
}

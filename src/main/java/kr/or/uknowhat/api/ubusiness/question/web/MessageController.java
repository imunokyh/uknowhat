package kr.or.uknowhat.api.ubusiness.question.web;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import kr.or.uknowhat.api.ubusiness.common.MessageType;
import kr.or.uknowhat.api.ubusiness.question.service.RoomQuestionService;
import kr.or.uknowhat.api.ubusiness.question.vo.MessageVo;
import kr.or.uknowhat.api.ubusiness.question.vo.RoomQuestionResMapping;
import kr.or.uknowhat.api.ubusiness.util.CommandTimer;
import lombok.extern.slf4j.Slf4j;


@Controller
@Slf4j
public class MessageController {
	
	@Autowired
	private SimpMessagingTemplate simpMessageTemplate;
	
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	
	@Autowired
	private RoomQuestionService roomQuestionService;
	
	HashMap<String, CommandTimer> timerMap = new HashMap<String, CommandTimer>();
	
	private static final String KEY_PROBLEM = "_problem";
	private static final String KEY_ANSWERS = "_answers";
	private static final String KEY_START_TS = "_start_ts";
	private static final String KEY_ELAPSED_TS = "_elapsed_ts";
	
	@MessageMapping(value = "/play/join")
	public void join(@Payload MessageVo message) {
		if (message.getType() == MessageType.JOIN) {
			redisTemplate.opsForZSet().add(message.getRoomNumber(), message.getParticipantName(), 0);
			message.setContent(message.getParticipantName() + "님이 입장하셨습니다.");
		} else if (message.getType() == MessageType.UNJOIN){
			redisTemplate.opsForZSet().remove(message.getRoomNumber(), message.getParticipantName());
			message.setContent(message.getParticipantName() + "님이 퇴장하셨습니다.");
		}
		
		simpMessageTemplate.convertAndSend("/subscribe/play/room/" + message.getRoomNumber(), message);
	}
		
	@MessageMapping(value = "/play/message")
	public void message(@Payload MessageVo message) throws ParseException, JsonMappingException, JsonProcessingException {
		log.info(message.getType().toString());
		if (message.getType() == MessageType.CHAT) {

		} else if (message.getType() == MessageType.ANSWER) {
			Long start = Long.valueOf(String.valueOf(redisTemplate.opsForValue().get(message.getRoomNumber() + KEY_START_TS)));
			redisTemplate.opsForHash().put(message.getRoomNumber() + KEY_ELAPSED_TS, message.getParticipantName(), System.currentTimeMillis() - start);
			redisTemplate.opsForHash().put(message.getRoomNumber() + KEY_ANSWERS, message.getParticipantName(), message.getContent());
		} else if (message.getType() == MessageType.START) {
			
		} else if (message.getType() == MessageType.READPROB) {
			message.setResult(roomQuestionService.readQuestion(message.getRoomNumber()));
		} else if (message.getType() == MessageType.OXP) {
			Long rqId = Long.valueOf(message.getContent());
			
			redisTemplate.opsForValue().set(message.getRoomNumber() + KEY_PROBLEM, rqId);
			
			if (redisTemplate.hasKey(message.getRoomNumber() + KEY_ANSWERS))
				redisTemplate.delete(message.getRoomNumber() + KEY_ANSWERS);
			if (redisTemplate.hasKey(message.getRoomNumber() + KEY_ELAPSED_TS))
				redisTemplate.delete(message.getRoomNumber() + KEY_ELAPSED_TS);
			
			RoomQuestionResMapping rqrm = roomQuestionService.readOneQuestion(Long.valueOf(message.getContent()));
			message.setContent(rqrm.getQuestionText());
		} else if (message.getType() == MessageType.OBP) {
			Long rqId = Long.valueOf(message.getContent());
			
			redisTemplate.opsForValue().set(message.getRoomNumber() + KEY_PROBLEM, rqId);
			
			if (redisTemplate.hasKey(message.getRoomNumber() + KEY_ANSWERS))
				redisTemplate.delete(message.getRoomNumber() + KEY_ANSWERS);
			if (redisTemplate.hasKey(message.getRoomNumber() + KEY_ELAPSED_TS))
				redisTemplate.delete(message.getRoomNumber() + KEY_ELAPSED_TS);
			
			RoomQuestionResMapping rqrm = roomQuestionService.readOneQuestion(Long.valueOf(message.getContent()));
			message.setContent(rqrm.getQuestionText());
			message.setAnswer1Text(rqrm.getAnswer1Text());
			message.setAnswer2Text(rqrm.getAnswer2Text());
			message.setAnswer3Text(rqrm.getAnswer3Text());
			message.setAnswer4Text(rqrm.getAnswer4Text());
		} else if (message.getType() == MessageType.TIMER) {
			long howSeconds =  Long.parseLong(message.getContent()) * 1000; // mili second
			int delay = 0; // delay time mili second
			int interval = 1000; // interval mili second
			
			if (howSeconds != 0) {
				CommandTimer ct = new CommandTimer(simpMessageTemplate, message.getRoomNumber(), Long.parseLong(message.getContent()));
				ct.start(howSeconds, delay, interval);
				
				timerMap.put(message.getRoomNumber(), ct);
			}
			
			redisTemplate.opsForValue().set(message.getRoomNumber() + KEY_START_TS, System.currentTimeMillis());
		} else if (message.getType() == MessageType.TIMEROFF) {
			CommandTimer ct = null; 
			
			if (timerMap.containsKey(message.getRoomNumber()))
				ct = timerMap.get(message.getRoomNumber());
			
			if (ct != null) {
				ct.end();
				timerMap.remove(message.getRoomNumber());
			}	
				
			message.setType(MessageType.TIMEOUT);
		} else if (message.getType() == MessageType.TIMECNT) {
			
		} else if (message.getType() == MessageType.TIMEOUT) {
			if (timerMap.containsKey(message.getRoomNumber()))
				timerMap.remove(message.getRoomNumber());
		} else if (message.getType() == MessageType.ANSCHK) {
			Map<Object, Object> ptAnswers = redisTemplate.opsForHash().entries(message.getRoomNumber() + KEY_ANSWERS);
			Map<Object, Object> ptElaspedTs = redisTemplate.opsForHash().entries(message.getRoomNumber() + KEY_ELAPSED_TS);
			
			RoomQuestionResMapping rqrm = roomQuestionService.readOneQuestion(Long.valueOf(String.valueOf(redisTemplate.opsForValue().get(message.getRoomNumber() + KEY_PROBLEM))));
			String questionAnswer = rqrm.getQuestionAnswer();
			Long questionScore = rqrm.getQuestionScore();
			Long questionTime = rqrm.getQuestionTime() * 1000;
			
			for (Map.Entry<Object, Object> entry : ptAnswers.entrySet()) {
				if (questionAnswer.equals(String.valueOf(entry.getValue()))) {
					Long elapsedTs = Long.valueOf(String.valueOf(ptElaspedTs.get(entry.getKey())));
					Long remainTs = (questionTime - elapsedTs) < 0 ? 0 : questionTime - elapsedTs;
					Long realScore = questionScore;
					
					if (questionTime == 0)
						// 제한 시간 없는 문제는 무조건 문제의 점수로 부여
						realScore = questionScore;
					else
						// 점수 산출 방식 = 기본 점수 (문제 점수 / 2) + 제출 시간에 따른 추가 점수 (나머지 점수 * 문제 시간 대비, 남은 시간 비율)
						realScore = (long) ((questionScore / 2.0) * ((double)remainTs / (double)questionTime)) + (long) (questionScore / 2.0);
					
					log.info(String.valueOf(entry.getKey()) + ": " + realScore);
					
					message.setType(MessageType.CORRECT);
					message.setScore(realScore);
					
					redisTemplate.opsForZSet().incrementScore(message.getRoomNumber(), entry.getKey(), realScore);
					
					simpMessageTemplate.convertAndSend("/subscribe/play/room/" + message.getRoomNumber() + "/" + String.valueOf(entry.getKey()), message);
					log.info("CORRECT " + "/subscribe/play/room/" + message.getRoomNumber() + "/" + String.valueOf(entry.getKey()));
				} else {
					message.setType(MessageType.INCORRECT);
					simpMessageTemplate.convertAndSend("/subscribe/play/room/" + message.getRoomNumber() + "/" + String.valueOf(entry.getKey()), message);
					log.info("INCORRECT " + "/subscribe/play/room/" + message.getRoomNumber() + "/" + String.valueOf(entry.getKey()));
				}
			}
			
			return;
		} else if (message.getType() == MessageType.ANSCHART) {
			Map<Object, Object> ptAnswers = redisTemplate.opsForHash().entries(message.getRoomNumber() + KEY_ANSWERS);
			HashMap<Integer, Integer> answerDt = new HashMap<Integer, Integer>();
			
			for (Map.Entry<Object, Object> entry : ptAnswers.entrySet()) {
				Integer key = Integer.valueOf(String.valueOf(entry.getValue()));
				if (answerDt.containsKey(key)) {
					answerDt.put(key, answerDt.get(key) + 1);
				} else {
					answerDt.put(key, 1);
				}
			}
			
			message.setResult(answerDt);	
		} else if (message.getType() == MessageType.RANK) {
			Set<TypedTuple<Object>> rankSet = redisTemplate.opsForZSet().reverseRangeWithScores(message.getRoomNumber(), 0, -1);
			message.setResult(rankSet);
		} else if (message.getType() == MessageType.FINALRANK) {
			Set<TypedTuple<Object>> rankSet = redisTemplate.opsForZSet().reverseRangeWithScores(message.getRoomNumber(), 0, -1);
			Double prevScore = (double) 0, curScore = (double) 0;
			int rank = 1, accumulateRank = 1;
			
			for (TypedTuple<Object> tuple : rankSet) {
				curScore = Double.valueOf(String.valueOf(tuple.getScore()));
				
				if (prevScore != curScore)
					rank = accumulateRank;
					
				message.setContent(String.valueOf(rank));
				simpMessageTemplate.convertAndSend("/subscribe/play/room/" + message.getRoomNumber() + "/" + String.valueOf(tuple.getValue()), message);
				log.info("FINALRANK " + "/subscribe/play/room/" + message.getRoomNumber() + "/" + String.valueOf(tuple.getValue()));
				
				accumulateRank++;
				prevScore = curScore;
			}
			
			return;
		} else if (message.getType() == MessageType.EXIT) {
			if (redisTemplate.hasKey(message.getRoomNumber()))
				redisTemplate.delete(message.getRoomNumber());
			if (redisTemplate.hasKey(message.getRoomNumber() + KEY_ANSWERS))
				redisTemplate.delete(message.getRoomNumber() + KEY_ANSWERS);
			if (redisTemplate.hasKey(message.getRoomNumber() + KEY_PROBLEM))
				redisTemplate.delete(message.getRoomNumber() + KEY_PROBLEM);
			if (redisTemplate.hasKey(message.getRoomNumber() + KEY_START_TS))
				redisTemplate.delete(message.getRoomNumber() + KEY_START_TS);
			if (redisTemplate.hasKey(message.getRoomNumber() + KEY_ELAPSED_TS))
				redisTemplate.delete(message.getRoomNumber() + KEY_ELAPSED_TS);
		}

		simpMessageTemplate.convertAndSend("/subscribe/play/room/" + message.getRoomNumber(), message);
	}
}

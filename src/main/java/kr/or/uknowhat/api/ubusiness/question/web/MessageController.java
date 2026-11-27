package kr.or.uknowhat.api.ubusiness.question.web;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

import kr.or.uknowhat.api.framework.exception.BizException;
import kr.or.uknowhat.api.ubusiness.common.ErrorCode;
import kr.or.uknowhat.api.ubusiness.common.GameSessionRegistry;
import kr.or.uknowhat.api.ubusiness.common.MessageType;
import kr.or.uknowhat.api.ubusiness.question.service.GameService;
import kr.or.uknowhat.api.ubusiness.question.vo.MessageVo;
import lombok.extern.slf4j.Slf4j;

/**
 * 게임 진행 STOMP 메시지 처리.
 * 실제 진행 로직(카운트다운/타이머/채점)은 {@link GameService} 가 서버에서 수행하며,
 * 이 컨트롤러는 요청 검증과 오류 응답만 담당한다.
 */
@Controller
@Slf4j
public class MessageController {

	private static final int MAX_CHAT_LENGTH = 500;

	@Autowired
	private SimpMessagingTemplate simpMessageTemplate;

	@Autowired
	private GameService gameService;

	@Autowired
	private GameSessionRegistry sessionRegistry;

	@MessageMapping(value = "/play/join")
	public void join(@Payload MessageVo message, SimpMessageHeaderAccessor accessor) {
		String sessionId = accessor.getSessionId();
		try {
			requireMessage(message);
			String roomNumber = message.getRoomNumber();
			String name = message.getParticipantName();

			if (message.getType() == MessageType.JOIN) {
				boolean rejoin = gameService.join(roomNumber, name, message.getToken(), sessionId);
				MessageVo joinMsg = new MessageVo();
				joinMsg.setType(MessageType.JOIN);
				joinMsg.setRoomNumber(roomNumber);
				joinMsg.setParticipantName(name);
				joinMsg.setContent(name + (rejoin ? "님이 다시 접속하셨습니다." : "님이 입장하셨습니다."));
				simpMessageTemplate.convertAndSend("/subscribe/play/room/" + roomNumber, joinMsg);
			} else if (message.getType() == MessageType.UNJOIN) {
				gameService.leave(roomNumber, name, message.getToken(), sessionId);
			} else {
				throw new BizException(ErrorCode.WRONG_REQUEST, "지원하지 않는 요청입니다.");
			}
		} catch (Exception e) {
			sendError(sessionId, message, e);
		}
	}

	/** 출제자 화면 접속 등록 */
	@MessageMapping(value = "/play/examiner")
	public void examiner(@Payload MessageVo message, SimpMessageHeaderAccessor accessor) {
		String sessionId = accessor.getSessionId();
		Principal principal = accessor.getUser(); // 참여자는 null
		try {
			requireMessage(message);
			gameService.registerExaminer(message.getRoomNumber(), principal, sessionId);
		} catch (Exception e) {
			sendError(sessionId, message, e);
		}
	}

	@MessageMapping(value = "/play/message")
	public void message(@Payload MessageVo message, SimpMessageHeaderAccessor accessor) {
		String sessionId = accessor.getSessionId();
		Principal principal = accessor.getUser(); // 참여자는 null
		try {
			requireMessage(message);
			String roomNumber = message.getRoomNumber();

			switch (message.getType()) {
			case CHAT:
				chat(message, sessionId, principal);
				break;
			case ANSWER:
				gameService.submitAnswer(roomNumber, message.getParticipantName(), sessionId,
						message.getQuestionIndex(), message.getContent());
				break;
			case START:
				gameService.start(roomNumber, principal);
				break;
			case READPROB:
				// 이전 버전 호환: START 에서 문제 목록을 함께 전송하므로 별도 처리 없음
				break;
			case NEXT:
			case FINISH:
				gameService.next(roomNumber, principal, message.getContent());
				break;
			case TIMEROFF:
				gameService.closeQuestion(roomNumber, principal);
				break;
			case EXIT:
				gameService.assertOwner(roomNumber, principal);
				gameService.closeRoom(roomNumber);
				break;
			default:
				throw new BizException(ErrorCode.WRONG_REQUEST, "지원하지 않는 메시지 타입입니다: " + message.getType());
			}
		} catch (Exception e) {
			sendError(sessionId, message, e);
		}
	}

	/** 메시지 변환 실패 등 핸들러 진입 전 오류 */
	@MessageExceptionHandler
	public void handleException(Exception e, SimpMessageHeaderAccessor accessor) {
		sendError(accessor.getSessionId(), null, e);
	}

	private void chat(MessageVo message, String sessionId, Principal principal) {
		String content = message.getContent();
		if (!StringUtils.hasText(content))
			return;
		if (content.length() > MAX_CHAT_LENGTH)
			throw new BizException(ErrorCode.WRONG_REQUEST, "채팅은 " + MAX_CHAT_LENGTH + "자까지 입력할 수 있습니다.");

		String name = message.getParticipantName();
		if (GameService.EXAMINER_NAME.equals(name)) {
			// 방장 이름 사칭 방지
			gameService.assertOwner(message.getRoomNumber(), principal);
		} else if (!sessionRegistry.isSessionOf(sessionId, message.getRoomNumber(), name)) {
			throw new BizException(ErrorCode.INVALID_SESSION, "참여 정보가 확인되지 않아 채팅을 보낼 수 없습니다.");
		}

		MessageVo chatMsg = new MessageVo();
		chatMsg.setType(MessageType.CHAT);
		chatMsg.setRoomNumber(message.getRoomNumber());
		chatMsg.setParticipantName(name);
		chatMsg.setChatType(message.getChatType());
		chatMsg.setContent(content);
		simpMessageTemplate.convertAndSend("/subscribe/play/room/" + message.getRoomNumber(), chatMsg);
	}

	private void requireMessage(MessageVo message) {
		if (message == null || message.getType() == null || !StringUtils.hasText(message.getRoomNumber()))
			throw new BizException(ErrorCode.WRONG_REQUEST, ErrorCode.WRONG_REQUEST.getMessage());
	}

	/**
	 * 요청한 세션에게만 오류를 전달한다. (클라이언트는 /user/queue/errors 구독)
	 */
	private void sendError(String sessionId, MessageVo request, Exception e) {
		String errorMessage;
		ErrorCode errorCode;
		if (e instanceof BizException) {
			BizException be = (BizException) e;
			errorCode = be.getBizErrorCode();
			errorMessage = be.getMessage();
			log.info("STOMP 요청 처리 실패 [{}] {} - {}", sessionId, request == null ? null : request.getType(), errorMessage);
		} else {
			errorCode = ErrorCode.ERROR;
			errorMessage = "요청 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.";
			log.error("STOMP 요청 처리 중 오류 [{}] {}", sessionId, request, e);
		}

		if (sessionId == null)
			return;

		try {
			MessageVo error = new MessageVo();
			error.setType(MessageType.ERROR);
			error.setRoomNumber(request == null ? null : request.getRoomNumber());
			error.setContent(errorMessage);
			error.setScore((long) errorCode.getCode());
			if (request != null && request.getType() != null)
				error.setResult(request.getType().name());

			SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
			headers.setSessionId(sessionId);
			headers.setLeaveMutable(true);
			simpMessageTemplate.convertAndSendToUser(sessionId, "/queue/errors", error, headers.getMessageHeaders());
		} catch (Exception sendEx) {
			log.warn("오류 메시지 전송 실패 [{}]: {}", sessionId, sendEx.getMessage());
		}
	}
}

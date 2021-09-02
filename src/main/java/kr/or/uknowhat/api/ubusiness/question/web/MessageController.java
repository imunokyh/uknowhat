package kr.or.uknowhat.api.ubusiness.question.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import kr.or.uknowhat.api.ubusiness.question.vo.MessageVo;
import lombok.extern.slf4j.Slf4j;


@Controller
@Slf4j
public class MessageController {
	
	@Autowired
	private SimpMessagingTemplate simpMessageTemplate;
	
	@MessageMapping(value = "/chat/join")
	public void join(@Payload MessageVo message) {
		message.setContent(message.getParticipantName() + "님이 입장하셨습니다.");
		simpMessageTemplate.convertAndSend("/subscribe/chat/room/" + message.getRoomNumber(), message);
	}
	
	@MessageMapping(value = "/chat/message")
	public void message(@Payload MessageVo message) {
		simpMessageTemplate.convertAndSend("/subscribe/chat/room/" + message.getRoomNumber(), message);
	}
}

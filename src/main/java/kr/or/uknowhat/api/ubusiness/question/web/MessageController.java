package kr.or.uknowhat.api.ubusiness.question.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import kr.or.uknowhat.api.ubusiness.common.MessageType;
import kr.or.uknowhat.api.ubusiness.question.vo.MessageVo;
import lombok.extern.slf4j.Slf4j;


@Controller
@Slf4j
public class MessageController {
	
	@Autowired
	private SimpMessagingTemplate simpMessageTemplate;
	
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	
	@MessageMapping(value = "/chat/join")
	public void join(@Payload MessageVo message) {
		HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
		
		if (message.getType() == MessageType.JOIN) {
			hashOperations.put(message.getRoomNumber(), message.getParticipantName(), "0");
			message.setContent(message.getParticipantName() + "님이 입장하셨습니다.");
		} else if (message.getType() == MessageType.UJOI){
			hashOperations.delete(message.getRoomNumber(), message.getParticipantName());
			message.setContent(message.getParticipantName() + "님이 퇴장하셨습니다.");
		}
		
		simpMessageTemplate.convertAndSend("/subscribe/chat/room/" + message.getRoomNumber(), message);
	}
		
	@MessageMapping(value = "/chat/message")
	public void message(@Payload MessageVo message) {
		log.info(message.toString());
		if(message.getType() == MessageType.CHAT){

		}else if (message.getType() == MessageType.ANSW){
			
		}else if (message.getType() == MessageType.START){
			
		}else if (message.getType() == MessageType.READPROB){
			
		}else if (message.getType() == MessageType.OXP){
			
		}else if (message.getType() == MessageType.SBP){
			
		}else if (message.getType() == MessageType.TIMECNT){
			
		}else if (message.getType() == MessageType.TIMEOUT){
			
		}else if (message.getType() == MessageType.ANSCHK){
			
		}else if (message.getType() == MessageType.ANSCHART){
			
		}else if (message.getType() == MessageType.PWAITING){
			
		}else if (message.getType() == MessageType.SENDANS){
			
		}
		
		simpMessageTemplate.convertAndSend("/subscribe/chat/room/" + message.getRoomNumber(), message);
	}
}

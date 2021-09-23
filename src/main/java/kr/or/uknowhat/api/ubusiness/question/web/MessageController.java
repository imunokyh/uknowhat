package kr.or.uknowhat.api.ubusiness.question.web;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
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
	
	@MessageMapping(value = "/play/join")
	public void join(@Payload MessageVo message) {
		HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
		
		if (message.getType() == MessageType.JOIN) {
			hashOperations.put(message.getRoomNumber(), message.getParticipantName(), 0);
			message.setContent(message.getParticipantName() + "님이 입장하셨습니다.");
		} else if (message.getType() == MessageType.UNJOIN){
			hashOperations.delete(message.getRoomNumber(), message.getParticipantName());
			message.setContent(message.getParticipantName() + "님이 퇴장하셨습니다.");
		}
		
		simpMessageTemplate.convertAndSend("/subscribe/play/room/" + message.getRoomNumber(), message);
	}
		
	@MessageMapping(value = "/play/message")
	public void message(@Payload MessageVo message) throws ParseException, JsonMappingException, JsonProcessingException {
		log.info(message.getType().toString());
		if (message.getType() == MessageType.CHAT) {

		} else if (message.getType() == MessageType.ANSWER) {
			redisTemplate.opsForHash().put(message.getRoomNumber() + "_answers", message.getParticipantName(), message.getContent());
		} else if (message.getType() == MessageType.START) {
			
		} else if (message.getType() == MessageType.READPROB) {
			message.setResult(roomQuestionService.readQuestion(message.getRoomNumber()));
		} else if (message.getType() == MessageType.OXP) {
			Long rqId = Long.valueOf(message.getContent());
			
			redisTemplate.opsForValue().set(message.getRoomNumber() + "_problem", rqId);
			redisTemplate.delete(message.getRoomNumber() + "_answers");
			
			RoomQuestionResMapping rqrm = roomQuestionService.readOneQuestion(Long.valueOf(message.getContent()));
			message.setContent(rqrm.getQuestionText());
		} else if (message.getType() == MessageType.OBP) {
			Long rqId = Long.valueOf(message.getContent());
			
			redisTemplate.opsForValue().set(message.getRoomNumber() + "_problem", rqId);
			redisTemplate.delete(message.getRoomNumber() + "_answers");
			
			RoomQuestionResMapping rqrm = roomQuestionService.readOneQuestion(Long.valueOf(message.getContent()));
			message.setContent(rqrm.getQuestionText());
			message.setAnswer1Text(rqrm.getAnswer1Text());
			message.setAnswer2Text(rqrm.getAnswer2Text());
			message.setAnswer3Text(rqrm.getAnswer3Text());
			message.setAnswer4Text(rqrm.getAnswer4Text());
		} else if (message.getType() == MessageType.TIMER) {
			
			long howSeconds =  Long.parseLong(message.getContent()) * 1000; // mili second
			int delay = 2000; // delay time mili second
			int interval = 1000; // interval mili second
			CommandTimer ct = new CommandTimer(simpMessageTemplate, message.getRoomNumber(), Long.parseLong(message.getContent()));
			ct.start(howSeconds,delay,interval);
			
		} else if (message.getType() == MessageType.TIMECNT) {
			
		} else if (message.getType() == MessageType.TIMEOUT) {
			
		} else if (message.getType() == MessageType.ANSCHK) {
			Map<Object, Object> ptAnswers = redisTemplate.opsForHash().entries(message.getRoomNumber() + "_answers");
			RoomQuestionResMapping rqrm = roomQuestionService.readOneQuestion(Long.valueOf(String.valueOf(redisTemplate.opsForValue().get(message.getRoomNumber() + "_problem"))));
			String questionAnswer = rqrm.getQuestionAnswer();
			Long questionScore = rqrm.getQuestionScore();
			
			for (Map.Entry<Object, Object> entry : ptAnswers.entrySet()) {
				if (questionAnswer.equals(String.valueOf(entry.getValue()))) {
					message.setType(MessageType.CORRECT);
					message.setScore(questionScore);
					
					Long curScore = Long.valueOf(String.valueOf(redisTemplate.opsForHash().get(message.getRoomNumber(), String.valueOf(entry.getKey()))));
					curScore += questionScore;
					redisTemplate.opsForHash().put(message.getRoomNumber(), String.valueOf(entry.getKey()), curScore);
					
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
			Map<Object, Object> ptAnswers = redisTemplate.opsForHash().entries(message.getRoomNumber() + "_answers");
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
		} else if (message.getType() == MessageType.PWAITING) {
			
		} else if (message.getType() == MessageType.SENDANS) {
			
		} else if (message.getType() == MessageType.EXIT) {
			redisTemplate.delete(message.getRoomNumber());
			redisTemplate.delete(message.getRoomNumber() + "_answers");
			redisTemplate.delete(message.getRoomNumber() + "_problem");
		}
		
		simpMessageTemplate.convertAndSend("/subscribe/play/room/" + message.getRoomNumber(), message);
	}
}

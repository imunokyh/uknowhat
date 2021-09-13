package kr.or.uknowhat.api.ubusiness.question.vo;

import kr.or.uknowhat.api.ubusiness.common.MessageType;
import lombok.Data;

@Data
public class MessageVo {
	
	private String roomNumber;
	private String participantName; // 참가자명
	private String examinerId; // 출제자아이디
	private String content;
	private String answer1Text;
	private String answer2Text;
	private String answer3Text;
	private String answer4Text;
	private MessageType type;
}

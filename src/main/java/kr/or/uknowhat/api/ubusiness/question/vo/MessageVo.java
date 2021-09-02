package kr.or.uknowhat.api.ubusiness.question.vo;

import kr.or.uknowhat.api.ubusiness.common.MessageType;
import lombok.Data;

@Data
public class MessageVo {
	
	private String roomNumber;
	
	private String participantName;
	
	private String content;
	
	private MessageType type;
}

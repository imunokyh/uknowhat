package kr.or.uknowhat.api.ubusiness.question.vo;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonProperty;

import kr.or.uknowhat.api.ubusiness.common.MessageType;
import lombok.Data;

@Data
public class MessageVo {
	private String roomNumber;
	private String participantName; // 참가자명
	private String examinerId; 		// 출제자아이디
	private String content;
	private Object result = new HashMap();
	private Long   score;
	private String answer1Text;
	private String answer2Text;
	private String answer3Text;
	private String answer4Text;
	private String chatType;
	private MessageType type;
	/** 현재 문제 순번 (0부터 시작) */
	private Integer questionIndex;
	/** 참여자 재접속용 토큰 - 클라이언트 → 서버 방향으로만 사용하며 브로드캐스트되지 않는다. */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String token;
}

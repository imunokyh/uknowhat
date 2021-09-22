package kr.or.uknowhat.api.ubusiness.question.vo;

import lombok.Data;

@Data
public class PlayAnswerVo {
	private String roomNumber;
	private String participantName;
	private Long questionNumber;
	private String answerText;
}

package kr.or.uknowhat.api.ubusiness.question.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class QuestionVo {
	@ApiModelProperty(value = "문제 아이디")
	private Long id;
	@ApiModelProperty(value = "문제 범주")
	private String category;
	@ApiModelProperty(value = "문제 유형")
	private String questionType;
	@ApiModelProperty(value = "시간 제한")
	private String timeLimitType;
	@ApiModelProperty(value = "포인트 유형")
	private String pointType;
	@ApiModelProperty(value = "문제 내용")
	private String questionText;
	@ApiModelProperty(value = "문제 정답")
	private String answer;
}

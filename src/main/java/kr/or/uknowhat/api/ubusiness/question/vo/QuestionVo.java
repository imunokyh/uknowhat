package kr.or.uknowhat.api.ubusiness.question.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class QuestionVo {
	@ApiModelProperty(value = "문제 아이디")
	private Long questionId;
	@ApiModelProperty(value = "문제 범주")
	private String questionCategory;
	@ApiModelProperty(value = "문제 유형")
	private String questionType;
	@ApiModelProperty(value = "시간 제한")
	private String timeLimitType;
	@ApiModelProperty(value = "포인트 유형")
	private String pointType;
	@ApiModelProperty(value = "문제 내용")
	private String questionText;
	@ApiModelProperty(value = "1번 보기")
	private String answer1Text;
	@ApiModelProperty(value = "2번 보기")
	private String answer2Text;
	@ApiModelProperty(value = "3번 보기")
	private String answer3Text;
	@ApiModelProperty(value = "4번 보기")
	private String answer4Text;
	@ApiModelProperty(value = "문제 정답")
	private String answer;
}

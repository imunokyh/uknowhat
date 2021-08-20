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
	private String type;
	@ApiModelProperty(value = "문제 내용")
	private String text;
	@ApiModelProperty(value = "문제 정답")
	private String answer;
}

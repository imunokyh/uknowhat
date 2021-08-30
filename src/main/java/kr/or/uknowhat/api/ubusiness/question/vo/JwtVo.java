package kr.or.uknowhat.api.ubusiness.question.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JwtVo {
	@ApiModelProperty(value = "인증 토큰")
	private String token;
}

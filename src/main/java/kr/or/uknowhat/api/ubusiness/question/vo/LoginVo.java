package kr.or.uknowhat.api.ubusiness.question.vo;


import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class LoginVo {
	@NotNull
	@Size(min = 0, max = 40)
	@ApiModelProperty(value = "유저 아이디")
	private String id;
	@NotNull
	@Size(min = 0, max = 256)
	@ApiModelProperty(value = "유저 비밀번호")
	private String password;
}

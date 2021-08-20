package kr.or.uknowhat.api.ubusiness.question.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserVo {
	@ApiModelProperty(value = "유저 아이디")
	private String id;
	@ApiModelProperty(value = "유저 패스워드")
	private String password;
	@ApiModelProperty(value = "유저 역할")
	private String role;
}

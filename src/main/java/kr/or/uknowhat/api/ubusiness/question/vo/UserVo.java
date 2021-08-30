package kr.or.uknowhat.api.ubusiness.question.vo;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import kr.or.uknowhat.api.ubusiness.common.Authority;
import lombok.Data;

@Data
public class UserVo {
	@NotNull
	@ApiModelProperty(value = "유저 아이디")
	private String id;
	
	@NotNull
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@ApiModelProperty(value = "유저 패스워드")
	private String password;
	
	@ApiModelProperty(value = "유저 역할")
	private Authority authority;
}

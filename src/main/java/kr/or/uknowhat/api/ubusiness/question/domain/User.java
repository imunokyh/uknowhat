package kr.or.uknowhat.api.ubusiness.question.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

import io.swagger.annotations.ApiModelProperty;
import kr.or.uknowhat.api.ubusiness.common.Authority;
import lombok.Data;

/**
 * 유저 정보 저장을 위한 엔티티
 * 
 * @author Yunho
 */
@Entity(name = "user")
@Data
public class User {
	@ApiModelProperty(value = "유저 아이디")
	@Id
	@Column(nullable = false, length = 40)
	private String id;
	
	@ApiModelProperty(value = "유저 비밀번호")
	@Column(nullable = false, length = 256)
	private String password;
	
	@ApiModelProperty(value = "유저 권한")
	@Enumerated(EnumType.STRING)
	@Column(nullable = true, length = 40)
	private Authority authority;
	
	@ApiModelProperty(value = "마지막 접근 시간")
	@Column(nullable = true)
	private Date lastAccessDate;
}

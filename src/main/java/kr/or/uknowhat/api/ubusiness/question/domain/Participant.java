package kr.or.uknowhat.api.ubusiness.question.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.ColumnDefault;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 참가자 정보 저장을 위한 엔티티
 * 
 * @author Yunho
 */
@Entity(name = "participant")
@Data
public class Participant {
	@ApiModelProperty(value = "참가자 아이디")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private Long participantId;
	
	@ApiModelProperty(value = "참가자 이름")
	@Column(nullable = false, length = 40)
	private String participantName;
	
	@ApiModelProperty(value = "참가자 점수")
	@Column(nullable = true)
	@ColumnDefault("0")
	private Integer participantScore;
}

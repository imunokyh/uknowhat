package kr.or.uknowhat.api.ubusiness.question.domain;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import org.hibernate.annotations.ColumnDefault;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 방에서 사용하는 문제 지정을 위한 엔티티
 * 
 * @author Yunho
 */
@Entity(name = "room_question")
@Data
public class RoomQuestion {
	@ApiModelProperty(value = "방, 문제 아이디 복합키")
	@EmbeddedId
	private RoomQuestionPrimaryKey roomQuestionId;
	
	@ApiModelProperty(value = "문제 순서")
	@Column(nullable = false)
	@ColumnDefault("0")
	private Integer	questionOrder;
	
	@ApiModelProperty(value = "문제 점수")
	@Column(nullable = true)
	@ColumnDefault("0")
	private Integer questionScore;
	
	@ApiModelProperty(value = "문제 제한시간 (초)")
	@Column(nullable = true)
	@ColumnDefault("10")
	private Integer questionTime;
}

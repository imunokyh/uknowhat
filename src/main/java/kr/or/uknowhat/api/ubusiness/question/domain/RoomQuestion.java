package kr.or.uknowhat.api.ubusiness.question.domain;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import io.swagger.annotations.ApiModelProperty;
import kr.or.uknowhat.api.ubusiness.question.vo.RoomQuestionVo;
import kr.or.uknowhat.api.ubusiness.util.ModelMapperUtils;
import lombok.Data;

/**
 * 방에서 사용하는 문제 지정을 위한 엔티티
 * 
 * @author Yunho
 */
@Entity(name = "room_question")
@Data
public class RoomQuestion {
	
	@ApiModelProperty(value = "아이디")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, length = 20)
	private Long id;
	
	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "room_id", nullable = false)
	private Room room;
	
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "question_id", nullable = false)
	private Question question;
	
	@ApiModelProperty(value = "문제 순서")
	@Column(nullable = false)
	@ColumnDefault("0")
	private Integer	questionOrder;
	
	@ApiModelProperty(value = "문제 점수")
	@Column(nullable = true)
	@ColumnDefault("10")
	private Integer questionScore;
	
	@ApiModelProperty(value = "문제 제한시간 (초)")
	@Column(nullable = true)
	@ColumnDefault("5")
	private Integer questionTime;
	
    public static RoomQuestion ofRoomQuestionVo(RoomQuestionVo roomQuestionVo) {
    	RoomQuestion roomQuestion = ModelMapperUtils.getModelMapper().map(roomQuestionVo, RoomQuestion.class);
        return roomQuestion;
    }
}

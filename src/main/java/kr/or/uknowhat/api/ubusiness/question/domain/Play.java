package kr.or.uknowhat.api.ubusiness.question.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.ColumnDefault;

import io.swagger.annotations.ApiModelProperty;
import kr.or.uknowhat.api.ubusiness.question.vo.RoomQuestionResMapping;
import kr.or.uknowhat.api.ubusiness.util.ModelMapperUtils;
import lombok.Data;

@Entity(name = "play")
@Data
public class Play {
	
	@ApiModelProperty(value = "아이디")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, length = 20)
	private Long id;
	
	@Column(length = 20)
	private Long roomId;
	
	@Column(length = 4)
	private String roomNumber;
	
	@Column(nullable = false, length = 20)
	private Long questionId;
	
	
	@ApiModelProperty(value = "문제 순서 - db 값")
	@Column(nullable = false)
	@ColumnDefault("0")
	private Long	questionOrder;
	
	@ApiModelProperty(value = "문제 순서 - 실제 순서")
	@Column(nullable = false)
	@ColumnDefault("0")
	private Long	questionNumber;
	
	@ApiModelProperty(value = "문제 점수")
	@Column(nullable = true)
	@ColumnDefault("10")
	private Long questionScore;
	
	@ApiModelProperty(value = "문제 제한시간 (초)")
	@Column(nullable = true)
	@ColumnDefault("5")
	private Long questionTime;
	
	@ApiModelProperty(value = "문제 내용")
	@Column(nullable = true, length=2000)
	private String questionText;
	
	@ApiModelProperty(value = "1번보기")
	@Column(nullable = true, length=500)
	private String answer1Text;
	
	@ApiModelProperty(value = "2번보기")
	@Column(nullable = true, length=500)
	private String answer2Text;
	
	@ApiModelProperty(value = "3번보기")
	@Column(nullable = true, length=500)
	private String answer3Text;
	
	@ApiModelProperty(value = "4번보기")
	@Column(nullable = true, length=500)
	private String answer4Text;
	
	@ApiModelProperty(value = "문제 유형: OX. OX 문제, OB. 객관식 문제, SB. 주관식 문제")
	@Column(nullable = false, length = 40)
	private String questionType;
	
	@ApiModelProperty(value = "문제 정답")
	@Column(nullable = true, length=40)
	private String questionAnswer;
	
	@ApiModelProperty(value = "처리코드")
	@Column(columnDefinition ="varchar(40) default 'N'") // N: 미처리 P: 처리 C: 현재진행
	private String procCode;
	
	public static Play ofRoomQuestionResMapping(RoomQuestionResMapping roomQuestionResMapping) {
		Play play = ModelMapperUtils.getModelMapper().map(roomQuestionResMapping, Play.class);
        return play;
    }
}

package kr.or.uknowhat.api.ubusiness.question.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import io.swagger.annotations.ApiModelProperty;
import kr.or.uknowhat.api.ubusiness.question.vo.PlayAnswerVo;
import kr.or.uknowhat.api.ubusiness.util.ModelMapperUtils;
import lombok.Data;

/**
 * 참가자가 제시한 답을 저장해 놓는다.
 * @author hygjob
 *
 */
@Entity(name = "play_answer")
@Data
public class PlayAnswer {

	@ApiModelProperty(value = "아이디")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, length = 20)
	private Long id;
	
	@Column(length = 20)
	private Long roomId;
	
	@Column(length = 4)
	private String roomNumber;
	
	@Column(length = 40)
	private String participantName;
	
	@Column(length = 20)
	private Long questionId;
	
	@Column(length = 20)
	private Long questionNumber;
	
	@Column(length = 40)
	private String answerText; // 참여자 제시 답
	
    public static PlayAnswer ofPlayAnswerVo(PlayAnswerVo playAnswerVo) {
    	PlayAnswer playAnswer = ModelMapperUtils.getModelMapper().map(playAnswerVo, PlayAnswer.class);
        return playAnswer;
    }
}

package kr.or.uknowhat.api.ubusiness.question.vo;

import javax.persistence.Column;

import io.swagger.annotations.ApiModelProperty;
import kr.or.uknowhat.api.ubusiness.question.domain.Question;
import kr.or.uknowhat.api.ubusiness.question.domain.Room;
import kr.or.uknowhat.api.ubusiness.question.domain.RoomQuestion;
import kr.or.uknowhat.api.ubusiness.util.ModelMapperUtils;
import lombok.Data;

@Data
public class RoomQuestionVo {
	
	@ApiModelProperty(value = "아이디")
	private Long id;
	private Long roomId;
	private Long questionId;
	@ApiModelProperty(value = "문제 순서")
	private Integer	questionOrder;
	@ApiModelProperty(value = "문제 점수")
	private Integer questionScore;
	@ApiModelProperty(value = "문제 제한시간 (초)")
	private Integer questionTime;
	
    public static RoomQuestionVo ofRoomQuestion(RoomQuestion roomQuestion) {
    	RoomQuestionVo roomQuestionVo = ModelMapperUtils.getModelMapper().map(roomQuestion, RoomQuestionVo.class);
        return roomQuestionVo;
    }
}

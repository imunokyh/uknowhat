package kr.or.uknowhat.api.ubusiness.question.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RoomVo {
	@ApiModelProperty(value = "방 아이디")
	private Long id;
	@ApiModelProperty(value = "방 제목")
	private String title;
	@ApiModelProperty(value = "방 상태")
	private String state;
}

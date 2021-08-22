package kr.or.uknowhat.api.ubusiness.question.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SampleVo {
    @ApiModelProperty(value = "Sample 시퀀스 넘버")
    private Long id;
    @ApiModelProperty(value = "질의")
    private String question;
    @ApiModelProperty(value = "응답")
    private String answer;
}

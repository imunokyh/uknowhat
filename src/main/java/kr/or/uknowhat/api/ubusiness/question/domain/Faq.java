package kr.or.uknowhat.api.ubusiness.question.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity(name = "faq_tb")
@Data
public class Faq {

    @ApiModelProperty(value = "FAQ 아이디")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false, length = 20)
    private Long id;

    @ApiModelProperty(value = "FAQ 질문텍스트")
    @Column(nullable = false, length = 2000)
    @NotNull(message = "Required String parameter 'question' is not present")
    private String question;

    @ApiModelProperty(value = "FAQ 답변텍스트")
    @Column(nullable = false, length = 2000)
    @NotNull(message = "Required String parameter 'answer' is not present")
    private String answer;

    @ApiModelProperty(value = "FAQ 생성일자")
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdDate;

    @ApiModelProperty(value = "FAQ 생성사용자아이디")
    @Column(length = 40)
    private String createdUserId;

    @ApiModelProperty(value = "FAQ 변경일자")
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    private Date modifiedDate;

    @ApiModelProperty(value = "FAQ 변경사용자아이디")
    @Column(length = 40)
    private String modifiedUserId;

    @ApiModelProperty(value = "이전 글 조회")
    @Transient
    private Faq preFaq;

    @ApiModelProperty(value = "다음 글 조회")
    @Transient
    private Faq nextFaq;

}
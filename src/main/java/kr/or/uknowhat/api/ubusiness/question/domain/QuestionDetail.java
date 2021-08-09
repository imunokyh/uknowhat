package kr.or.uknowhat.api.ubusiness.question.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 객관식 질문 보기
 *
 */
@Entity(name = "question_detail_tb")
@Data
public class QuestionDetail {

	@ApiModelProperty(value = "아이디")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
	
	@ApiModelProperty(value = "객관식문제아이디")
	@Column(nullable = false)
    private Long parent_id;
	
    @ApiModelProperty(value = "문제번호")
    @Column(nullable = false, length=2)
    private Integer questionNum;
    
    @ApiModelProperty(value = "객관식보기내용")
    @Column(nullable = true, length = 4000)
    private String questionText;
    
    @ApiModelProperty(value = "생성일자")
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdDate;

    @ApiModelProperty(value = "생성 유저 아이디")
    @Column(length = 40)
    private String createdUserId;

    @ApiModelProperty(value = "수정일자")
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    private Date modifiedDate;

    @ApiModelProperty(value = "수정 유저 아이디")
    @Column(length = 40)
    private String modifiedUserId;
}

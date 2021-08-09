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
 * 질문
 *
 */
@Entity(name = "question_tb")
@Data
public class Question {

	@ApiModelProperty(value = "아이디")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
	
    @ApiModelProperty(value = "문제유형 1: 객관식(답한개) 2: 객관식(답여러개) 3: 주관식")
    @Column(nullable = true, length = 40)
    private String questionType;
    
    @ApiModelProperty(value = "문제카테고리")
    @Column(nullable = true, length = 40)
    private String category;
    
    @ApiModelProperty(value = "문제내용")
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

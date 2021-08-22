package kr.or.uknowhat.api.ubusiness.question.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 문제 저장을 위한 엔티티
 * 
 * @author Yunho
 */
@Entity(name = "question")
@Data
public class Question {
	@ApiModelProperty(value = "문제 아이디")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private Long questionId;
	
	@ApiModelProperty(value = "문제 카테고리")
	@Column(nullable = true, length = 40)
	private String questionCategory;
	
	@ApiModelProperty(value = "문제 유형: 0. OX 문제, 1. 객관식 문제, 2. 주관식 문제")
	@Column(nullable = false, length = 40)
	private String questionType;
	
	@ApiModelProperty(value = "시간제한 유형: 20: 20초, 5: 5초 0: 없음")
	@Column(nullable = false, length = 40)
	private String timeLimitType;
	
	@ApiModelProperty(value = "포인트 유형: 0: 스탠다드, 1: 더블 2: 없음")
	@Column(nullable = false, length = 40)
	private String pointType;
	
	@ApiModelProperty(value = "문제 내용")
	@Column(nullable = true, columnDefinition = "LONGTEXT")
	private String questionText;
	
	@ApiModelProperty(value = "문제 정답")
	@Column(nullable = false, columnDefinition = "LONGTEXT")
	private String questionAnswer;
	
	@ApiModelProperty(value = "생성일자")
	@Temporal(TemporalType.TIMESTAMP)
	@CreatedDate
	@Column(nullable = false, updatable = false)
	private Date createdDate;
	
	@ApiModelProperty(value = "생성 유저 아이디")
	@Column(nullable = false, updatable = false, length = 40)
	private String createdUserId;
	
	@ApiModelProperty(value = "수정일자")
	@Temporal(TemporalType.TIMESTAMP)
	@LastModifiedDate
	@Column(nullable = true)
	private Date modifiedDate;
	
	@ApiModelProperty(value = "수정 유저 아이디")
	@Column(nullable = true, length = 40)
	private String modifiedUserId;
}

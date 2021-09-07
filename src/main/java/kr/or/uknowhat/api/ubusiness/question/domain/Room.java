package kr.or.uknowhat.api.ubusiness.question.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 방 생성을 위한 엔티티
 * 
 * @author Yunho
 */
@Entity(name = "room")
@Data
public class Room {
	@ApiModelProperty(value = "방 아이디")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private Long roomId;
	
	@ApiModelProperty(value = "방 번호")
	@Column(nullable = true, length = 4)
	private String roomNumber;
	
	@ApiModelProperty(value = "방 제목")
	@Column(nullable = false, length = 40)
	private String roomTitle;
	
	@ApiModelProperty(value = "방 상태: NULL(초기), READY(대기), PLAY(시작), END(종료)")
	@Column(nullable = true, length = 40)
	private String roomState;
	
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
	
	@ApiModelProperty(value = "시작일자")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = true)
	private Date startedDate;
	
	@ApiModelProperty(value = "종료일자")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = true)
	private Date endedDate;
}

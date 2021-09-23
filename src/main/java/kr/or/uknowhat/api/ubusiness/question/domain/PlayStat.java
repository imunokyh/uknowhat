package kr.or.uknowhat.api.ubusiness.question.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.ColumnDefault;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 문제당 채점
 * @author hygjob
 *
 */
@Entity(name = "play_stat")
@Data
public class PlayStat {

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
	@ColumnDefault("0")
	private Long score;
	
	@Column(length = 20)
	private Long questionNumber;
	
	@Column(nullable = true, length=40)
	private String questionAnswer; // 문제정답
	
	@Column(length = 40)
	private String answerText; // 참여자 제시 답
	
	@Column(length = 40)
	private String correctYn; // 정답여부
	
	
}

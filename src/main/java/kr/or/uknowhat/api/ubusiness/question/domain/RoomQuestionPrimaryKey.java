package kr.or.uknowhat.api.ubusiness.question.domain;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;

/**
 * 방에서 사용하는 문제에 대한 복합키 정의
 * 
 * @author Yunho
 */
@Data
@Embeddable
public class RoomQuestionPrimaryKey implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3645444003357499768L;

//	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
//	@JoinColumn(name = "room_id", nullable = false)
//	private Room room;
//	
//	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
//	@JoinColumn(name = "question_id", nullable = false)
//	private Question question;
}

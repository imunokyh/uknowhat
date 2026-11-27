package kr.or.uknowhat.api.ubusiness.question.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * 게임 시작 시점의 문제 스냅샷.
 * 진행 중 문제가 수정되더라도 게임은 시작 시점의 문제로 일관되게 진행된다.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GameQuestion {
	public static final String TYPE_OX = "OX";
	public static final String TYPE_OB = "OB";

	private Long id;
	private String questionType;
	private String questionText;
	private String answer1Text;
	private String answer2Text;
	private String answer3Text;
	private String answer4Text;
	private String questionAnswer;
	private Long questionScore;
	private Long questionTime;

	public static GameQuestion of(RoomQuestionResMapping rq) {
		GameQuestion q = new GameQuestion();
		q.setId(rq.getId());
		q.setQuestionType(rq.getQuestionType());
		q.setQuestionText(rq.getQuestionText());
		q.setAnswer1Text(rq.getAnswer1Text());
		q.setAnswer2Text(rq.getAnswer2Text());
		q.setAnswer3Text(rq.getAnswer3Text());
		q.setAnswer4Text(rq.getAnswer4Text());
		q.setQuestionAnswer(rq.getQuestionAnswer());
		q.setQuestionScore(rq.getQuestionScore() == null ? 0L : rq.getQuestionScore());
		q.setQuestionTime(rq.getQuestionTime() == null || rq.getQuestionTime() < 0 ? 0L : rq.getQuestionTime());
		return q;
	}

	@JsonIgnore
	public boolean isSupportedType() {
		return TYPE_OX.equals(questionType) || TYPE_OB.equals(questionType);
	}

	/** 선택 가능한 보기 개수 */
	@JsonIgnore
	public int choiceCount() {
		return TYPE_OX.equals(questionType) ? 2 : 4;
	}
}

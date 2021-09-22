package kr.or.uknowhat.api.ubusiness.question.service;

import kr.or.uknowhat.api.ubusiness.question.vo.PlayAnswerVo;

public interface PlayService {

	void readProb(Long roomId, String roomNumber) ;
	void readNext(String roomNumber);
	void checkAnswer(String roomNumber);
	void writeAnswer(PlayAnswerVo playAnswerVo);
	void whoWinQuiz(String roomNumber);
}

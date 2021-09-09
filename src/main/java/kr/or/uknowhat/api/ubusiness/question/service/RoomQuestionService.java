package kr.or.uknowhat.api.ubusiness.question.service;

import kr.or.uknowhat.api.ubusiness.question.domain.RoomQuestion;
import kr.or.uknowhat.api.ubusiness.question.vo.RoomQuestionVo;

public interface RoomQuestionService {
	//Page<RoomQuestion> listQuestion(int page, int size, String searchType, String searchText);
	RoomQuestion getQuestion(Long id);
	RoomQuestionVo insertQuestion(RoomQuestionVo rqVo);
	RoomQuestionVo updateQuestion(RoomQuestionVo rqVo);
	void deleteQuestion(Long id);
}

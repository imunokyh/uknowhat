package kr.or.uknowhat.api.ubusiness.question.service;

import java.util.List;

import kr.or.uknowhat.api.ubusiness.question.domain.RoomQuestion;
import kr.or.uknowhat.api.ubusiness.question.vo.RoomQuestionResMapping;
import kr.or.uknowhat.api.ubusiness.question.vo.RoomQuestionVo;

public interface RoomQuestionService {
	//Page<RoomQuestion> listQuestion(int page, int size, String searchType, String searchText);
	List<RoomQuestionResMapping> listQuestion(Long roomId);
	List<RoomQuestionResMapping> readQuestion(String roomNumber);
	RoomQuestionResMapping readOneQuestion(Long id);
	RoomQuestion getQuestion(Long id);
	RoomQuestionVo insertQuestion(RoomQuestionVo rqVo);
	RoomQuestionVo updateQuestion(RoomQuestionVo rqVo);
	void deleteQuestion(Long id);
}

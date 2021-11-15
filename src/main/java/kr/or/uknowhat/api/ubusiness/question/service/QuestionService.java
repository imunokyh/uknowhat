package kr.or.uknowhat.api.ubusiness.question.service;

import org.springframework.data.domain.Page;

import kr.or.uknowhat.api.ubusiness.question.domain.Question;
import kr.or.uknowhat.api.ubusiness.question.vo.QuestionVo;

public interface QuestionService {
	Page<Question> listQuestion(int page, int size, String searchType, String searchText);
	Page<Question> listMyQuestion(int page, int size, String searchType, String searchText);
	Question getQuestion(Long id);
	void insertQuestion(QuestionVo questionVo);
	void updateQuestion(QuestionVo questionVo);
	void deleteQuestion(Long id);
}

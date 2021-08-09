package kr.or.uknowhat.api.ubusiness.question.service;

import java.util.List;

import kr.or.uknowhat.api.ubusiness.question.domain.Question;

public interface QuestionService {

	List<Question> listQuestion() throws Exception;
}

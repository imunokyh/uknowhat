package kr.or.uknowhat.api.ubusiness.question.service.impl;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.or.uknowhat.api.ubusiness.question.domain.Question;
import kr.or.uknowhat.api.ubusiness.question.repositories.QuestionRepository;
import kr.or.uknowhat.api.ubusiness.question.service.QuestionService;
import kr.or.uknowhat.api.ubusiness.question.vo.QuestionVo;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class QuestionServiceImpl implements QuestionService {

	@Autowired
	private QuestionRepository questionRepository;

	@Override
	public Page<Question> listQuestion(int page, int size, String searchText) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
		return questionRepository.findAllByQuestionTextContaining(pageable, searchText);
	}

	@Override
	public Question getQuestion(Long id) {
		Question question = null;
		
		Optional<Question> optionalQuestion = questionRepository.findById(id);
		if (optionalQuestion.isPresent()) {
			question = optionalQuestion.get();
		}
		
		return question;
	}

	@Override
	public void insertQuestion(QuestionVo questionVo) {
		Question question = new Question();
		question.setQuestionCategory(questionVo.getQuestionCategory());
		question.setQuestionType(questionVo.getQuestionType());
		question.setTimeLimitType(questionVo.getTimeLimitType());
		question.setPointType(questionVo.getPointType());
		question.setQuestionText(questionVo.getQuestionText());
		question.setAnswer1Text(questionVo.getAnswer1Text());
		question.setAnswer2Text(questionVo.getAnswer2Text());
		question.setAnswer3Text(questionVo.getAnswer3Text());
		question.setAnswer4Text(questionVo.getAnswer4Text());
		question.setQuestionAnswer(questionVo.getAnswer());
		question.setCreatedDate(new Date());
		question.setCreatedUserId("admin");
		questionRepository.save(question);
	}

	@Override
	public void updateQuestion(QuestionVo questionVo) {
		Optional<Question> optionalQuestion = questionRepository.findById(questionVo.getQuestionId());
		if (optionalQuestion.isPresent()) {
			Question question = optionalQuestion.get();
			question.setQuestionCategory(questionVo.getQuestionCategory());
			question.setQuestionType(questionVo.getQuestionType());
			question.setTimeLimitType(questionVo.getTimeLimitType());
			question.setPointType(questionVo.getPointType());
			question.setQuestionText(questionVo.getQuestionText());
			question.setQuestionAnswer(questionVo.getAnswer());
			question.setModifiedDate(new Date());
			question.setModifiedUserId("admin");
			questionRepository.save(question);
		}
	}

	@Override
	public void deleteQuestion(Long id) {
		Optional<Question> optionalQuestion = questionRepository.findById(id);
		if (optionalQuestion.isPresent()) {
			questionRepository.delete(optionalQuestion.get());
		}
	}
}

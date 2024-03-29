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
import org.springframework.util.StringUtils;

import kr.or.uknowhat.api.ubusiness.question.domain.Question;
import kr.or.uknowhat.api.ubusiness.question.repositories.QuestionRepository;
import kr.or.uknowhat.api.ubusiness.question.service.QuestionService;
import kr.or.uknowhat.api.ubusiness.question.vo.QuestionVo;
import kr.or.uknowhat.api.ubusiness.util.SecurityUtil;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class QuestionServiceImpl implements QuestionService {

	@Autowired
	private QuestionRepository questionRepository;

	@Override
	public Page<Question> listQuestion(int page, int size, String searchType,  String searchText) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
		
		Page<Question> questionPage = null;
		if(StringUtils.hasText(searchType)&& StringUtils.hasText(searchText)) {
			questionPage = questionRepository.findAllByQuestionTypeAndQuestionTextContaining(pageable, searchType, searchText);
		}else if (StringUtils.hasText(searchType)) {
			questionPage = questionRepository.findAllByQuestionType(pageable, searchType);
		}else {
			questionPage = questionRepository.findAllByQuestionTextContaining(pageable, searchText);
		}
		return questionPage;
				
	}
	
	@Override
	public Page<Question> listMyQuestion(int page, int size, String searchType,  String searchText) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
		String userId = SecurityUtil.getUserId();
		
		Page<Question> questionPage = null;
		if(StringUtils.hasText(searchType) && StringUtils.hasText(searchText)) {
			questionPage = questionRepository.findAllByQuestionTypeAndQuestionTextContainingAndCreatedUserId(pageable, searchType, searchText, userId);
		} else if (StringUtils.hasText(searchType)) {
			questionPage = questionRepository.findAllByQuestionTypeAndCreatedUserId(pageable, searchType, userId);
		} else {
			questionPage = questionRepository.findAllByQuestionTextContainingAndCreatedUserId(pageable, searchText, userId);
		}
		
		return questionPage;
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
		question.setCreatedUserId(SecurityUtil.getUserId());
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
			question.setModifiedUserId(SecurityUtil.getUserId());
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

package kr.or.uknowhat.api.ubusiness.question.service.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.or.uknowhat.api.ubusiness.question.domain.Question;
import kr.or.uknowhat.api.ubusiness.question.repositories.QuestionRepository;
import kr.or.uknowhat.api.ubusiness.question.service.QuestionService;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class QuestionServiceImpl implements QuestionService {

	@Autowired
	private QuestionRepository questionRepository;
	
	@Override
	public List<Question> listQuestion() throws Exception {
		return questionRepository.findAll();
	}
}

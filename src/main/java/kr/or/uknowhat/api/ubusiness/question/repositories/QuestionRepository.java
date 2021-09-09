package kr.or.uknowhat.api.ubusiness.question.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.or.uknowhat.api.ubusiness.question.domain.Question;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
	Page<Question> findAllByQuestionTextContaining(Pageable pageable, String searchText);
	Page<Question> findAllByQuestionType(Pageable pageable, String searchType);
	Page<Question> findAllByQuestionTypeAndQuestionTextContaining(Pageable pageable, String searchType, String searchText);
	
}

package kr.or.uknowhat.api.ubusiness.question.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.or.uknowhat.api.ubusiness.question.domain.PlayAnswer;

public interface PlayAnswerRepository extends JpaRepository<PlayAnswer, Long>{

	List<PlayAnswer> findAllByRoomNumberAndQuestionNumber(String roomNumber, Long questionNumber);
}

package kr.or.uknowhat.api.ubusiness.question.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.or.uknowhat.api.ubusiness.question.domain.Room;
import kr.or.uknowhat.api.ubusiness.question.domain.RoomQuestion;

@Repository
public interface RoomQuestionRepository extends JpaRepository<RoomQuestion, Long> {
	
	
	Page<RoomQuestion> findAllByRoom(Pageable pageable, Room room);
	
}

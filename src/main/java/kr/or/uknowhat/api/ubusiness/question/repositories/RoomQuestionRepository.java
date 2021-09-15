package kr.or.uknowhat.api.ubusiness.question.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import kr.or.uknowhat.api.ubusiness.question.domain.RoomQuestion;
import kr.or.uknowhat.api.ubusiness.question.vo.RoomQuestionResMapping;

@Repository
public interface RoomQuestionRepository extends JpaRepository<RoomQuestion, Long> {
	
	
	//Page<RoomQuestion> findAllByRoom(Pageable pageable, Room room);
	
	@Query(value="select rq.id as id" + 
			", rq.question_id as questionId " + 
			", rq.question_order as questionOrder" + 
			", rq.question_score as questionScore" + 
			", rq.question_time as questionTime" + 
			", rq.room_id as roomId" + 
			", q.question_text as questionText" + 
			", q.answer1text as answer1Text" + 
			", q.answer2text as answer2Text" + 
			", q.answer3text as answer3Text" + 
			", q.answer4text as answer4Text" + 
			", q.question_type as questionType" + 
			", q.question_answer as questionAnswer" + 
			" from room_question rq, question q" + 
			" where rq.question_id = q.question_id" +
			" and rq.room_id = :roomId" +
			" order by rq.question_order asc" +
			"", nativeQuery=true)
	List<RoomQuestionResMapping> findAllByRoomId(Long roomId);
	
	@Query(value = "select room_id from room where room_number = :roomNumber", nativeQuery = true)
	Optional<Long> findRoomIdByRoomNumber(String roomNumber);
}

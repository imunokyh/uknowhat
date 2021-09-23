package kr.or.uknowhat.api.ubusiness.question.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.or.uknowhat.api.ubusiness.question.domain.Play;

@Repository
public interface PlayRepository extends JpaRepository<Play, Long>{

	void deleteAllByRoomId(Long roomId);
	
	Play findTop1ByRoomNumberAndProcCodeNotInOrderByQuestionNumberAsc(String roomNumber, List<String> procCodeList);
	
	Optional<Play> findTop1ByRoomNumberAndProcCodeOrderByQuestionNumberAsc(String roomNumber, String procCode);
}

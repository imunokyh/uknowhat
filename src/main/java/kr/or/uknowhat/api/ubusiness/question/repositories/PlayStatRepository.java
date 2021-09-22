package kr.or.uknowhat.api.ubusiness.question.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import kr.or.uknowhat.api.ubusiness.question.domain.PlayStat;
import kr.or.uknowhat.api.ubusiness.question.vo.PlayStatWinnerMapping;

public interface PlayStatRepository extends JpaRepository<PlayStat, Long>{

	// rank and participantName and total score
	@Query(value ="select ranking, participantName, totalScore "
			+ " from "
			+ " (select participant_name as participantName, sum(score) as totalScore , rank() over(order by totalScore desc) as ranking "
			+ " from play_stat where room_number = :roomNumber "
			+ " group by participant_name"
			+ " order by ranking) x"
			+ " where x.ranking <=3 ", nativeQuery=true)
	List<PlayStatWinnerMapping> findAllTop3Ranker(String roomNumber);
}

package kr.or.uknowhat.api.ubusiness.question.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.or.uknowhat.api.ubusiness.question.domain.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
	List<Room> findAll();
}

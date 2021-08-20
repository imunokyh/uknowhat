package kr.or.uknowhat.api.ubusiness.question.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.or.uknowhat.api.ubusiness.question.domain.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
	Page<Room> findAllByCreatedUserId(Pageable pageable, String searchUser);
}

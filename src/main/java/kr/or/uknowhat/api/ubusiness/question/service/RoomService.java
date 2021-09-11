package kr.or.uknowhat.api.ubusiness.question.service;

import java.util.List;

import org.springframework.data.domain.Page;

import kr.or.uknowhat.api.ubusiness.question.domain.Room;
import kr.or.uknowhat.api.ubusiness.question.vo.RoomVo;

public interface RoomService {
	Page<Room> listRoom(int page, int size, String searchUser);
	List<Room> listMyRoom(); // 나의룸 가져오기
	Room getRoom(Long id);
	Room insertRoom(RoomVo roomVo);
	Room updateRoom(RoomVo roomVo);
	void deleteRoom(Long id);
	boolean existRoom(String roomNumber);
	Room getRoomByRoomNumber(String roomNumber);
}

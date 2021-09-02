package kr.or.uknowhat.api.ubusiness.question.service;

import org.springframework.data.domain.Page;

import kr.or.uknowhat.api.ubusiness.question.domain.Room;
import kr.or.uknowhat.api.ubusiness.question.vo.RoomVo;

public interface RoomService {
	Page<Room> listRoom(int page, int size, String searchUser);
	Room getRoom(Long id);
	Room insertRoom(RoomVo roomVo);
	Room updateRoom(RoomVo roomVo);
	void deleteRoom(Long id);
	boolean existRoom(String roomNumber);
	Room getRoomByRoomNumber(String roomNumber);
}

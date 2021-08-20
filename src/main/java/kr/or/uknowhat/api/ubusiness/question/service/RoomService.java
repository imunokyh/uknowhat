package kr.or.uknowhat.api.ubusiness.question.service;

import org.springframework.data.domain.Page;

import kr.or.uknowhat.api.ubusiness.question.domain.Room;
import kr.or.uknowhat.api.ubusiness.question.vo.RoomVo;

public interface RoomService {
	Page<Room> listRoom(int page, int size, String searchUser);
	Room getRoom(Long id);
	void insertRoom(RoomVo roomVo);
	void updateRoom(RoomVo roomVo);
	void deleteRoom(Long id);
}

package kr.or.uknowhat.api.ubusiness.question.service;

import java.util.List;

import kr.or.uknowhat.api.ubusiness.question.domain.Room;

public interface RoomService {
	List<Room> listRoom() throws Exception;
}

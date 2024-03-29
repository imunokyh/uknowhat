package kr.or.uknowhat.api.ubusiness.question.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.or.uknowhat.api.ubusiness.common.RandomNumberGenerator;
import kr.or.uknowhat.api.ubusiness.question.domain.Room;
import kr.or.uknowhat.api.ubusiness.question.repositories.RoomRepository;
import kr.or.uknowhat.api.ubusiness.question.service.RoomService;
import kr.or.uknowhat.api.ubusiness.question.vo.RoomVo;
import kr.or.uknowhat.api.ubusiness.util.SecurityUtil;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class RoomServiceImpl implements RoomService {
	
	@Autowired
	private RoomRepository roomRepository;

	@Override
	public Page<Room> listRoom(int page, int size, String searchUser) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
		return roomRepository.findAllByCreatedUserId(pageable, searchUser);
	}
	
	@Override
	public List<Room> listMyRoom() {
		
		String userId = SecurityUtil.getUserId();
		return roomRepository.findAllByCreatedUserId(userId);
	}

	@Override
	public Room getRoom(Long id) {
		Room room = null;
		
		Optional<Room> optionalRoom = roomRepository.findById(id);
		if (optionalRoom.isPresent()) {
			room = optionalRoom.get();
		}
		
		return room;
	}

	@Override
	public Room insertRoom(RoomVo roomVo) {		
		String userId = SecurityUtil.getUserId();
		
		Room room = new Room();
		room.setRoomTitle(roomVo.getTitle());
		room.setCreatedDate(new Date());
		room.setCreatedUserId(userId);
		return roomRepository.save(room);
	}

	@Override
	public Room updateRoom(RoomVo roomVo) {
		Optional<Room> optionalRoom = roomRepository.findById(roomVo.getId());
		if (optionalRoom.isPresent()) {
			Room room = optionalRoom.get();
			
			room.setRoomTitle(roomVo.getTitle());
			room.setModifiedDate(new Date());
			room.setModifiedUserId(SecurityUtil.getUserId());
			
			return roomRepository.save(room);
		}
		return null;
	}

	@Override
	public void deleteRoom(Long id) {
		Optional<Room> optionalRoom = roomRepository.findById(id);
		if (optionalRoom.isPresent()) {
			roomRepository.delete(optionalRoom.get());
		}
	}

	@Override
	public boolean existRoom(String roomNumber) {
		return roomRepository.existsByRoomNumber(roomNumber);
	}

	@Override
	public Room getRoomByRoomNumber(String roomNumber) {
		Optional<Room> optionalRoom = roomRepository.findByRoomNumber(roomNumber);
		if (optionalRoom.isPresent()) {
			return optionalRoom.get();
		} else {
			return null;
		}
	}

	@Override
	public Room changeRoomState(RoomVo roomVo) {
		Optional<Room> optionalRoom = roomRepository.findById(roomVo.getId());
		if (optionalRoom.isPresent()) {
			Room room = optionalRoom.get();
			
			if (!Objects.equals(room.getRoomState(), "PLAY") && Objects.equals(roomVo.getState(), "PLAY")) {
				RandomNumberGenerator randomNumberGenerator = new RandomNumberGenerator();
				
				String roomNumber = "";
				for (int i = 0; i < 10; i++) {
					roomNumber = randomNumberGenerator.generateNumber(4, true);
					if (!existRoom(roomNumber))
						break;
				}
				
				room.setStartedDate(new Date());
				room.setRoomNumber(roomNumber);
			}
			else if (Objects.equals(room.getRoomState(), "PLAY") && Objects.equals(roomVo.getState(), "END")) {
				room.setEndedDate(new Date());
				room.setRoomNumber(null);
			}
			
			room.setRoomState(roomVo.getState());
			
			return roomRepository.save(room);
		}
		return null;
	}
}

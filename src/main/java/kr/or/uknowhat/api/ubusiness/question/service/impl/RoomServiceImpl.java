package kr.or.uknowhat.api.ubusiness.question.service.impl;

import java.util.Arrays;
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

import kr.or.uknowhat.api.framework.exception.BizException;
import kr.or.uknowhat.api.ubusiness.common.ErrorCode;
import kr.or.uknowhat.api.ubusiness.common.RandomNumberGenerator;
import kr.or.uknowhat.api.ubusiness.question.domain.Room;
import kr.or.uknowhat.api.ubusiness.question.repositories.RoomRepository;
import kr.or.uknowhat.api.ubusiness.question.service.GameService;
import kr.or.uknowhat.api.ubusiness.question.service.RoomService;
import kr.or.uknowhat.api.ubusiness.question.vo.RoomVo;
import kr.or.uknowhat.api.ubusiness.util.SecurityUtil;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class RoomServiceImpl implements RoomService {
	
	private static final String STATE_PLAY = "PLAY";
	private static final List<String> ALLOWED_STATES = Arrays.asList("READY", "PLAY", "END");
	private static final int ROOM_NUMBER_RETRY = 20;
	
	@Autowired
	private RoomRepository roomRepository;
	
	@Autowired
	private GameService gameService;

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
		Room room = getOwnedRoom(roomVo.getId());
		
		room.setRoomTitle(roomVo.getTitle());
		room.setModifiedDate(new Date());
		room.setModifiedUserId(SecurityUtil.getUserId());
		
		return roomRepository.save(room);
	}

	@Override
	public void deleteRoom(Long id) {
		Room room = getOwnedRoom(id);
		String roomNumber = room.getRoomNumber();
		
		roomRepository.delete(room);
		
		// 진행 중인 방을 삭제한 경우 참여자에게 종료를 알리고 게임 데이터 정리
		if (roomNumber != null)
			gameService.closeRoom(roomNumber);
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
		String nextState = roomVo.getState();
		if (!ALLOWED_STATES.contains(nextState))
			throw new BizException(ErrorCode.WRONG_REQUEST, "올바르지 않은 방 상태입니다: " + nextState);
		
		Room room = getOwnedRoom(roomVo.getId());
		String currentState = room.getRoomState();
		
		if (Objects.equals(currentState, STATE_PLAY) && Objects.equals(nextState, STATE_PLAY)) {
			// 이미 진행 중인 방: 기존 방 번호 그대로 이어서 진행
			return room;
		}
		
		String endedRoomNumber = null;
		
		if (Objects.equals(nextState, STATE_PLAY)) {
			RandomNumberGenerator randomNumberGenerator = new RandomNumberGenerator();
			
			String roomNumber = null;
			for (int i = 0; i < ROOM_NUMBER_RETRY; i++) {
				String candidate = randomNumberGenerator.generateNumber(4, true);
				if (!existRoom(candidate)) {
					roomNumber = candidate;
					break;
				}
			}
			if (roomNumber == null)
				throw new BizException(ErrorCode.ERROR, "방 번호 생성에 실패했습니다. 잠시 후 다시 시도해 주세요.");
			
			// 같은 번호로 진행됐던 이전 게임의 잔여 데이터 정리
			gameService.resetRoom(roomNumber);
			
			room.setStartedDate(new Date());
			room.setEndedDate(null);
			room.setRoomNumber(roomNumber);
		}
		else if (Objects.equals(currentState, STATE_PLAY)) {
			// 진행 중 → 종료/대기
			endedRoomNumber = room.getRoomNumber();
			room.setEndedDate(new Date());
			room.setRoomNumber(null);
		}
		
		room.setRoomState(nextState);
		Room saved = roomRepository.save(room);
		
		if (endedRoomNumber != null)
			gameService.closeRoom(endedRoomNumber);
		
		return saved;
	}
	
	/**
	 * 본인이 생성한 방인지 확인 후 반환
	 */
	private Room getOwnedRoom(Long id) {
		if (id == null)
			throw new BizException(ErrorCode.WRONG_REQUEST, "방 아이디가 없습니다.");
		
		Room room = roomRepository.findById(id)
				.orElseThrow(() -> new BizException(ErrorCode.ROOM_NOT_FOUND, ErrorCode.ROOM_NOT_FOUND.getMessage()));
		
		if (!Objects.equals(room.getCreatedUserId(), SecurityUtil.getUserId()))
			throw new BizException(ErrorCode.FORBIDDEN, "본인이 생성한 방만 변경할 수 있습니다.");
		
		return room;
	}
}

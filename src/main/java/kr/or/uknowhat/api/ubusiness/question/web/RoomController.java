package kr.or.uknowhat.api.ubusiness.question.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.or.uknowhat.api.framework.vo.Result;
import kr.or.uknowhat.api.ubusiness.common.ErrorCode;
import kr.or.uknowhat.api.ubusiness.question.domain.Room;
import kr.or.uknowhat.api.ubusiness.question.service.RoomService;
import kr.or.uknowhat.api.ubusiness.question.vo.RoomVo;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = "/api/v1/room")
@Slf4j
public class RoomController {
	
	@Autowired
	private RoomService roomService;
	
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	
	@GetMapping
	public Result listRoom(@RequestParam(value = "page", defaultValue = "0") int page,
						   @RequestParam(value = "size", defaultValue = "10") int size,
						   @RequestParam(value = "user", required = false, defaultValue = "") String user) {
		Result res = new Result();
		res.setCode(ErrorCode.SUCCESS);
		res.setResult(roomService.listRoom(page, size, user));
		return res;
	}
	
	/**
	 * 나의룸가져오기
	 * @return
	 */
	@GetMapping(value = "/me")
	public Result listMyRoom() {
		Result res = new Result();
		res.setCode(ErrorCode.SUCCESS);
		res.setResult(roomService.listMyRoom());
		return res;
	}
	
	@GetMapping(value = "{id}")
	public Result getRoom(@PathVariable Long id) {
		Result res = new Result();
		res.setCode(ErrorCode.SUCCESS);
		res.setResult(roomService.getRoom(id));
		return res;
	}
	
	@PostMapping
	public Result insertRoom(@RequestBody @Valid RoomVo roomVo) {
		Room room = roomService.insertRoom(roomVo);
		Result res = new Result();
		res.setCode(ErrorCode.SUCCESS);
		res.setResult(room);
		return res;
	}
	
	@PutMapping(value = "/state")
	public Result changeRoomState(@RequestBody @Valid RoomVo roomVo) {
		Room room = roomService.changeRoomState(roomVo);
		Result res = new Result();
		
		if (room == null) {
			res.setCode(ErrorCode.ERROR);
		} else {
			res.setCode(ErrorCode.SUCCESS);
		}
		res.setResult(room);
		
		return res;
	}
	
	@PutMapping
	public Result updateRoom(@RequestBody @Valid RoomVo roomVo) {
		Room room = roomService.updateRoom(roomVo);
		Result res = new Result();
		
		if (room == null) {
			res.setCode(ErrorCode.ERROR);
		} else {
			res.setCode(ErrorCode.SUCCESS);
		}
		res.setResult(room);
		
		return res;
	}
	
	@DeleteMapping(value = "{id}")
	public Result deleteRoom(@PathVariable Long id) {
		roomService.deleteRoom(id);
		Result res = new Result();
		res.setCode(ErrorCode.SUCCESS);
		return res;
	}
	
	@GetMapping(value = "/check/number/{number}")
	public Result checkNumber(@PathVariable(value = "number") String roomNumber) {
		Result res = new Result();
		if (roomService.existRoom(roomNumber)) {
			Room room = null;
			RoomVo roomVo = new RoomVo();
			
			room = roomService.getRoomByRoomNumber(roomNumber);
			roomVo.setId(room.getRoomId());
			roomVo.setTitle(room.getRoomTitle());
			roomVo.setState(room.getRoomState());
			res.setCode(ErrorCode.SUCCESS);
			res.setResult(roomVo);
		} else {
			res.setCode(ErrorCode.ERROR);
			res.setMessage("방을 찾을 수 없습니다.");
		}
		return res;
	}
	
	@GetMapping(value = "/check/nickname")
	public Result checkNickname(@RequestParam(value = "number", defaultValue = "") String roomNumber,
								@RequestParam(value = "nickname", defaultValue = "") String nickname) {
		Result res = new Result();
		
		HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
		Object value = hashOperations.get(roomNumber, nickname);
		if (value == null) {
			res.setCode(ErrorCode.SUCCESS);
		} else {
			res.setCode(ErrorCode.ERROR);
			res.setMessage("중복되는 닉네임이 존재합니다.");
		}
		
		return res;
	}
	
	@GetMapping(value = "/check/ptlist")
	public Result checkParticipantList(@RequestParam(value = "number", defaultValue = "") String roomNumber) {
		Result res = new Result();
		List list = new ArrayList();
		
		HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
		Map<Object, Object> mapList = hashOperations.entries(roomNumber);
		
		for (Entry<Object, Object> entry : mapList.entrySet()) {
			list.add(entry.getKey());
		}
		
		res.setCode(ErrorCode.SUCCESS);
		res.setResult(list);
		
		return res;
	}
}

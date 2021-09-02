package kr.or.uknowhat.api.ubusiness.question.web;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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
	
	@GetMapping
	public Result listRoom(@RequestParam(value = "page", defaultValue = "0") int page,
						   @RequestParam(value = "size", defaultValue = "10") int size,
						   @RequestParam(value = "user", required = false, defaultValue = "") String user) {
		Result res = new Result();
		res.setCode(ErrorCode.SUCCESS);
		res.setResult(roomService.listRoom(page, size, user));
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
	
	@PutMapping
	public Result updateRoom(@RequestBody @Valid RoomVo roomVo) {
		Room room = roomService.updateRoom(roomVo);
		Result res = new Result();
		res.setCode(ErrorCode.SUCCESS);
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
	
	@GetMapping(value = "/number/{number}")
	public Result existRoom(@PathVariable String number) {
		Result res = new Result();
		if (roomService.existRoom(number)) {
			Room room = null;
			RoomVo roomVo = new RoomVo();
			
			room = roomService.getRoomByRoomNumber(number);
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
}

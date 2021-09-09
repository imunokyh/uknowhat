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
import kr.or.uknowhat.api.ubusiness.question.service.RoomQuestionService;
import kr.or.uknowhat.api.ubusiness.question.vo.RoomQuestionVo;
import kr.or.uknowhat.api.ubusiness.question.vo.RoomVo;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = "/api/v1/rq")
@Slf4j
public class RoomQuestionController {

	@Autowired
	private RoomQuestionService rqService;
	
	
	@GetMapping
	public Result listQuestion(@RequestParam(value = "page", defaultValue = "0") int page,
						   @RequestParam(value = "size", defaultValue = "10") int size,
						   @RequestParam(value = "roomId", required = false, defaultValue = "") Long roomId) {
		Result res = new Result();
		res.setCode(ErrorCode.SUCCESS);
		log.info("roomId:" + roomId);
		res.setResult(rqService.listQuestion(roomId));
		return res;
	}
	
	@GetMapping(value = "{id}")
	public Result getQuestion(@PathVariable Long id) {
		Result res = new Result();
		res.setCode(ErrorCode.SUCCESS);
		res.setResult(rqService.getQuestion(id));
		return res;
	}
	
	@PostMapping
	public Result insertQuestion(@RequestBody @Valid RoomQuestionVo rqVo) {
		log.info(rqVo.toString());
		Result res = new Result();
		res.setCode(ErrorCode.SUCCESS);
		res.setResult(rqService.insertQuestion(rqVo));
		return res;
	}
	
	@PutMapping
	public Result updateQuestion(@RequestBody @Valid RoomQuestionVo rqVo) {
		Result res = new Result();
		res.setCode(ErrorCode.SUCCESS);
		res.setResult(rqService.updateQuestion(rqVo));
		return res;
	}
	
	@DeleteMapping(value = "{id}")
	public Result deleteQuestion(@PathVariable Long id) {
		rqService.deleteQuestion(id);
		Result res = new Result();
		res.setCode(ErrorCode.SUCCESS);
		return res;
	}
	
	
}

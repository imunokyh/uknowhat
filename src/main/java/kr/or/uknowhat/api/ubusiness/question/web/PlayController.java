package kr.or.uknowhat.api.ubusiness.question.web;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.or.uknowhat.api.framework.vo.Result;
import kr.or.uknowhat.api.ubusiness.common.ErrorCode;
import kr.or.uknowhat.api.ubusiness.question.service.GameService;
import kr.or.uknowhat.api.ubusiness.question.service.PlayService;
import kr.or.uknowhat.api.ubusiness.question.service.RoomQuestionService;
import kr.or.uknowhat.api.ubusiness.question.vo.PlayAnswerVo;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = "/api/v1/play")
@Slf4j
public class PlayController {
	
	@Autowired
	private RoomQuestionService rqService;
	
	@Autowired
	private PlayService playService;

	@Autowired
	private GameService gameService;

	/**
	 * 출제자 재접속 시 현재 게임 진행 상태 조회
	 */
	@GetMapping(value="/state")
	public Result state(@RequestParam(value = "number", defaultValue = "") String roomNumber, Principal principal) {
		Result res = new Result();
		res.setCode(ErrorCode.SUCCESS);
		res.setResult(gameService.examinerState(roomNumber, principal));
		return res;
	}

	@GetMapping(value="/readprob")
	public Result readProb(@RequestParam(value = "roomId", defaultValue = "10") int roomId,
						   @RequestParam(value = "roomNumber", defaultValue = "") String roomNumer) {
		playService.readProb(Long.valueOf(roomId), roomNumer);
		Result res = new Result();
		res.setCode(ErrorCode.SUCCESS);
		//res.setResult(playService.readProb(Long.valueOf(roomId)));
		return res;
	}
	
	@GetMapping(value="/readnext")
	public Result readNext(@RequestParam(value = "roomNumber", defaultValue = "") String roomNumber) {
		playService.readNext(roomNumber);
		Result res = new Result();
		res.setCode(ErrorCode.SUCCESS);
		//res.setResult(playService.readProb(Long.valueOf(roomId)));
		return res;
	}
	
	@GetMapping(value="/checkans")
	public Result checkAnswer(@RequestParam(value = "roomNumber", defaultValue = "") String roomNumber) {
		playService.checkAnswer(roomNumber);
		Result res = new Result();
		res.setCode(ErrorCode.SUCCESS);
		//res.setResult(playService.readProb(Long.valueOf(roomId)));
		return res;
	}
	
	@PostMapping(value="/writeAnswer")
	public Result writeAnswer(@RequestBody PlayAnswerVo playAnswerVo) {
		log.info(playAnswerVo.toString());
		playService.writeAnswer(playAnswerVo);
		Result res = new Result();
		res.setCode(ErrorCode.SUCCESS);
		//res.setResult(playService.readProb(Long.valueOf(roomId)));
		return res;
	}
	
	@PostMapping(value="/checkwinner")
	public Result checkWinner(@RequestBody PlayAnswerVo playAnswerVo) {
		log.info(playAnswerVo.toString());
		playService.whoWinQuiz(playAnswerVo.getRoomNumber());
		Result res = new Result();
		res.setCode(ErrorCode.SUCCESS);
		//res.setResult(playService.readProb(Long.valueOf(roomId)));
		return res;
	}
	
}

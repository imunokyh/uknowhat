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
import kr.or.uknowhat.api.ubusiness.question.service.QuestionService;
import kr.or.uknowhat.api.ubusiness.question.vo.QuestionVo;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = "/api/v1/question")
@Slf4j
public class QuestionController {
	
	@Autowired
	private QuestionService questionService;
	
	@GetMapping
	public Result listQuestion(@RequestParam(defaultValue = "0") int page,
							   @RequestParam(defaultValue = "10") int size,
							   @RequestParam(defaultValue = "") String searchType,
							   @RequestParam(defaultValue = "") String searchText
							   ) {
		Result res = new Result();
		res.setCode(ErrorCode.SUCCESS);
		res.setResult(questionService.listQuestion(page, size, searchType, searchText));
		return res;
	}
	
	@GetMapping(value = "{questionId}")
	public Result getQuestion(@PathVariable Long questionId) {
		Result res = new Result();
		res.setCode(ErrorCode.SUCCESS);
		res.setResult(questionService.getQuestion(questionId));
		return res;
	}
	
	@PostMapping
	public Result insertQuestion(@RequestBody @Valid QuestionVo questionVo) {

		questionService.insertQuestion(questionVo);
		Result res = new Result();
		res.setCode(ErrorCode.SUCCESS);
		return res;
	}
	
	@PutMapping
	public Result updateQuestion(@RequestBody @Valid QuestionVo questionVo) {
		questionService.updateQuestion(questionVo);
		Result res = new Result();
		res.setCode(ErrorCode.SUCCESS);
		return res;
	}
	
	@DeleteMapping(value = "{questionId}")
	public Result deleteQuestion(@PathVariable Long questionId) {
		questionService.deleteQuestion(questionId);
		Result res = new Result();
		res.setCode(ErrorCode.SUCCESS);
		return res;
	}
}

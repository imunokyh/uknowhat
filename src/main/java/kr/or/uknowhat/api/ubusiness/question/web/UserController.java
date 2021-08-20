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
import kr.or.uknowhat.api.ubusiness.question.service.UserService;
import kr.or.uknowhat.api.ubusiness.question.vo.UserVo;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = "/api/v1/user")
@Slf4j
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@GetMapping
	public Result listUser(@RequestParam(defaultValue = "0") int page,
						   @RequestParam(defaultValue = "10") int size) {
		Result res = new Result();
		res.setErrorCode(ErrorCode.SUCCESS);
		res.setMessage("code success");
		res.setResult(userService.listUser(page, size));
		return res;
	}
	
	@GetMapping(value = "{id}")
	public Result getUser(@PathVariable String id) {
		Result res = new Result();
		res.setCode(ErrorCode.SUCCESS);
		res.setResult(userService.getUser(id));
		return res;
	}
	
	@PostMapping
	public Result insertUser(@RequestBody @Valid UserVo userVo) {
		userService.insertUser(userVo);
		Result res = new Result();
		res.setCode(ErrorCode.SUCCESS);
		return res;
	}
	
	@PutMapping
	public Result updateUser(@RequestBody @Valid UserVo userVo) {
		userService.updateUser(userVo);
		Result res = new Result();
		res.setCode(ErrorCode.SUCCESS);
		return res;
	}
	
	@DeleteMapping(value = "{id}")
	public Result deleteUser(@PathVariable String id) {
		userService.deleteUser(id);
		Result res = new Result();
		res.setCode(ErrorCode.SUCCESS);
		return res;
	}
}

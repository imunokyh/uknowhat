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
import kr.or.uknowhat.api.ubusiness.question.vo.JwtVo;
import kr.or.uknowhat.api.ubusiness.question.vo.LoginVo;
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
	
	@PutMapping(value = "/edit")
	public Result updateUser(@RequestBody @Valid UserVo userVo) {
		Result res = new Result();
		
		if (userService.updateUser(userVo) == ErrorCode.ERROR.getCode()) {
			res.setCode(ErrorCode.ERROR);
		} else {
			res.setCode(ErrorCode.SUCCESS);
		}
		
		return res;
	}
	
	@DeleteMapping(value = "{id}")
	public Result deleteUser(@PathVariable String id) {
		Result res = new Result();
		
		if (userService.deleteUser(id) == ErrorCode.ERROR.getCode()) {
			res.setCode(ErrorCode.ERROR);
		} else {
			res.setCode(ErrorCode.SUCCESS);
		}
		
		return res;
	}
	
	@PostMapping(value = "/signup")
	public Result signup(@RequestBody @Valid UserVo userVo) {
		Result res = new Result();
		
		if (userService.insertUser(userVo) == ErrorCode.ERROR.getCode()) {
			res.setCode(ErrorCode.ERROR);
			res.setMessage("해당하는 유저 아이디가 이미 존재합니다.");
		} else {
			res.setCode(ErrorCode.SUCCESS);
		}
		
		return res;
	}
	
	@PostMapping(value = "/login")
	public Result login(@RequestBody @Valid LoginVo loginVo) {
		Result res = new Result();
		
		if (userService.getUser(loginVo.getId()) == null) {
			res.setCode(ErrorCode.ERROR);
			res.setMessage("아이디가 존재하지 않습니다.");
		} else {
			JwtVo jwt = null;
			
			jwt = userService.loginUser(loginVo);
			if (jwt == null) {
				res.setCode(ErrorCode.ERROR);
				res.setMessage("비밀번호가 틀렸습니다.");
			} else {
				res.setCode(ErrorCode.SUCCESS);
				res.setResult(jwt);
			}
		}
		
		return res;
	}
}

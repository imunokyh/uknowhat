package kr.or.uknowhat.api.ubusiness.question.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.or.uknowhat.api.framework.vo.Result;
import kr.or.uknowhat.api.ubusiness.common.ErrorCode;
import kr.or.uknowhat.api.ubusiness.question.service.UserService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = "/api/v1/user")
@Slf4j
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@GetMapping(value = "/list")
	public Result listUser() throws Exception {
		Result res = new Result();
		res.setErrorCode(ErrorCode.SUCCESS);
		res.setMessage("code success");
		res.setResult(userService.listUser());
		
		return res;
	}
}

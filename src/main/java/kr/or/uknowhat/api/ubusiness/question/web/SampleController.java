package kr.or.uknowhat.api.ubusiness.question.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.or.uknowhat.api.framework.vo.Result;
import kr.or.uknowhat.api.ubusiness.common.ErrorCode;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = "/api/v1/sample")
@Slf4j
public class SampleController {

	@GetMapping(value = "/test")
    public Result test() throws Exception {
    	Result ret = new Result();
    	ret.setErrorCode(ErrorCode.SUCCESS); 
    	ret.setMessage("code success"); 
        return ret;
    }
}

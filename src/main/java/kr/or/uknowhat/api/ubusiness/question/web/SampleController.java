package kr.or.uknowhat.api.ubusiness.question.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.or.uknowhat.api.framework.vo.Result;
import kr.or.uknowhat.api.ubusiness.common.ErrorCode;
import kr.or.uknowhat.api.ubusiness.question.vo.SampleVo;
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
	
	@PostMapping(value = "/post")
    public Result post(@RequestBody SampleVo sampleVo) throws Exception {
		log.info(sampleVo.toString());
		log.info(sampleVo.getQuestion());
		log.info(sampleVo.getAnswer());
    	Result ret = new Result();
    	ret.setErrorCode(ErrorCode.ERROR); 
    	ret.setMessage("code success"); 
        return ret;
    }
}

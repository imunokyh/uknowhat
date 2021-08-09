package kr.or.uknowhat.api.ubusiness.question.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.or.uknowhat.api.framework.vo.Result;
import kr.or.uknowhat.api.ubusiness.common.ErrorCode;
import kr.or.uknowhat.api.ubusiness.question.service.QuestionService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = "/api/qsn")
@Slf4j
public class QuestionController {

	@Autowired
	private QuestionService questionService;
	
    @GetMapping(value = "/question/list")
    public Result listQuestion() throws Exception {
        	Result ret = new Result();
        	ret.setErrorCode(ErrorCode.SUCCESS); 
        	ret.setMessage("code success");
        	ret.setResult(questionService.listQuestion());
        	
            return ret;
	}
}

package kr.or.uknowhat.api.ubusiness.util;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import kr.or.uknowhat.api.framework.vo.Result;
import kr.or.uknowhat.api.ubusiness.common.ErrorCode;

@Component
public class ErrorCodeUtil {
	
	
    public Result error(String errMessage) {
        Result result = new Result();
        result.setCode(ErrorCode.ERROR);
        result.setMessage(errMessage);
        return result;
    }
    
    public Result error(String errMessage, ErrorCode errorCode ) {
        Result result = new Result();
        result.setCode(errorCode.getCode());
        result.setMessage(errMessage);
        return result;
    }
    
    public Result error(String errMessage, HttpStatus httpStatus ) {
        Result result = new Result();
        result.setCode(httpStatus.value());
        result.setMessage(errMessage);
        return result;
    }
    
}

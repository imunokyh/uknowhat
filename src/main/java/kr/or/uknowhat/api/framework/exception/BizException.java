package kr.or.uknowhat.api.framework.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import kr.or.uknowhat.api.ubusiness.common.ErrorCode;

/**
 * Created by csupshin on 18/07/16.
 */
public class BizException extends RuntimeException {

    private final ResponseEntity<?> response;

    private final HttpStatus httpStatus ;
    
    private final ErrorCode errorCode;

    /**
     * BizException 생성자
     *
     * @param message
     */
    public BizException(String message) {
        super(message);
        this.response = null;
        this.httpStatus = null;
        this.errorCode = ErrorCode.ERROR;
    }
    
    public BizException(ErrorCode errorCode, String message) {
        super(message);
        this.response = null;
        this.httpStatus = null;
        this.errorCode = errorCode;
    }

    public BizException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
        this.response = null;
        this.errorCode = ErrorCode.ERROR;

    }

    public BizException(String message, Throwable cause) {
        super(message, cause);
        this.response = null;
        this.httpStatus = null;
        this.errorCode = ErrorCode.ERROR;
    }

    public BizException(String message, ResponseEntity<?> response) {
        super(message);
        this.response = response;
        this.httpStatus = null;
        this.errorCode = ErrorCode.ERROR;
    }

    public ResponseEntity<?> getResponse() {
        return response;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
    
    public ErrorCode getBizErrorCode() {
    	return this.errorCode;
    }

}

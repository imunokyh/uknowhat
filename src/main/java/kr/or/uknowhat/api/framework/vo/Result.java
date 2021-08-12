package kr.or.uknowhat.api.framework.vo;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonIgnore;

import kr.or.uknowhat.api.ubusiness.common.ErrorCode;
import lombok.Data;

@Data
public class Result {

	private int code;
	@JsonIgnore
	private ErrorCode errorCode;
	
	private String message;
	private Object result = new HashMap();
	
	public void setCode(ErrorCode errorCode) {
		this.errorCode = errorCode;
		this.code = errorCode.getCode();
	}
	public int getCode() {
		return this.errorCode.getCode();
	}

}


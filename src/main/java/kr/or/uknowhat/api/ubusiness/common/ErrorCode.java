package kr.or.uknowhat.api.ubusiness.common;

public enum ErrorCode {

	SUCCESS(0, "SUCCESS")
	, ERROR(1, "ERROR")
	, WRONG_REQUEST(1000, "잘못된 요청입니다.");
	;
	
	private int code;
	private String message;
	
	private ErrorCode(int code, String message) {

		this.code = code;
        this.message = message;
	}
	
	public int getCode() {
		return this.code;
	}
	
	public String getMessage() {
		return this.message;
	}
	  
	
	
}

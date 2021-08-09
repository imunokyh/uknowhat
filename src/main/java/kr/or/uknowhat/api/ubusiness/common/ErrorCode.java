package kr.or.uknowhat.api.ubusiness.common;

public enum ErrorCode {

	SUCCESS(0, "SUCCESS")
	, FAIL(1, "FAIL");
	
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

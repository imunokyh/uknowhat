package kr.or.uknowhat.api.ubusiness.common;

public enum ErrorCode {

	SUCCESS(0, "SUCCESS")
	, ERROR(1, "ERROR")
	, WRONG_REQUEST(1000, "잘못된 요청입니다.")
	, UNAUTHORIZED(1001, "인증 정보가 없습니다. 다시 로그인해 주세요.")
	, FORBIDDEN(1002, "권한이 없습니다.")
	, ROOM_NOT_FOUND(1003, "방을 찾을 수 없습니다.")
	, INVALID_STATE(1004, "현재 진행 상태에서 처리할 수 없는 요청입니다.")
	, DUPLICATE_NICKNAME(1005, "중복되는 닉네임이 존재합니다.")
	, INVALID_SESSION(1006, "참여 정보가 일치하지 않습니다. 다시 입장해 주세요.")
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

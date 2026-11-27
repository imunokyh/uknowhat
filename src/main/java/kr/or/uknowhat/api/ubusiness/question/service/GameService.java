package kr.or.uknowhat.api.ubusiness.question.service;

import java.security.Principal;
import java.util.Map;

/**
 * 실시간 퀴즈 진행을 서버에서 관리한다.
 *
 * 카운트다운 / 제한시간 / 채점 / 결과 전송을 모두 서버가 수행하므로
 * 출제자나 참여자의 연결이 끊겨도 진행 중인 문제는 계속 진행되고,
 * 재접속 시 현재 상태를 조회해 이어서 진행할 수 있다.
 */
public interface GameService {

	String EXAMINER_NAME = "★방 장★";

	/** 참여자 입장 (재접속 포함). 재접속이면 true */
	boolean join(String roomNumber, String name, String token, String sessionId);

	/** 참여자가 직접 나가기를 누른 경우 */
	void leave(String roomNumber, String name, String token, String sessionId);

	/** 출제자 STOMP 세션 등록 */
	void registerExaminer(String roomNumber, Principal principal, String sessionId);

	/** 닉네임 사용 가능 여부. 동일 토큰이면 재접속으로 판단해 true(rejoin) 를 반환한다. */
	boolean checkNickname(String roomNumber, String name, String token);

	void start(String roomNumber, Principal principal);

	/** 출제자의 다음 단계 요청. expectedPhase 가 현재 단계와 다르면(중복 클릭 등) 무시한다. */
	void next(String roomNumber, Principal principal, String expectedPhase);

	/** 출제자의 문제 조기 종료 요청 */
	void closeQuestion(String roomNumber, Principal principal);

	void submitAnswer(String roomNumber, String name, String sessionId, Integer questionIndex, String answer);

	Map<String, Object> examinerState(String roomNumber, Principal principal);

	Map<String, Object> participantState(String roomNumber, String name, String token);

	void assertOwner(String roomNumber, Principal principal);

	/** 방 종료: 참여자에게 EXIT 전송 후 게임 데이터 삭제 */
	void closeRoom(String roomNumber);

	/** 새 게임 시작 전 이전 게임의 잔여 데이터 삭제 */
	void resetRoom(String roomNumber);
}

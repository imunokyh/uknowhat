package kr.or.uknowhat.api.ubusiness.common;

/**
 * 서버가 관리하는 게임 진행 단계.
 * 진행 상태는 Redis 에 저장되므로 출제자/참여자의 연결 여부와 상관없이 유지된다.
 */
public enum GamePhase {
	/** 대기실 (시작 전) */
	WAITING,
	/** 문제 공개 전 카운트다운 */
	COUNTDOWN,
	/** 문제 풀이 중 */
	QUESTION,
	/** 채점 완료 (정답 분포 / 정답자 목록 공개) */
	RESULT,
	/** 중간 순위 공개 */
	RANK,
	/** 최종 순위 공개 (게임 종료) */
	FINISHED
}

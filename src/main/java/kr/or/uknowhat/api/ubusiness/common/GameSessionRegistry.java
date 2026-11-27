package kr.or.uknowhat.api.ubusiness.common;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import lombok.Value;

/**
 * STOMP 세션 ↔ (방 번호, 참여자) 매핑.
 * 참여자의 온라인 여부 판단과 메시지 발신자 검증에 사용한다.
 */
@Component
public class GameSessionRegistry {

	public enum Role { PARTICIPANT, EXAMINER }

	@Value
	public static class SessionInfo {
		String roomNumber;
		String name;
		Role role;
	}

	private final ConcurrentHashMap<String, SessionInfo> sessions = new ConcurrentHashMap<>();

	public void register(String sessionId, String roomNumber, String name, Role role) {
		if (sessionId == null) return;
		sessions.put(sessionId, new SessionInfo(roomNumber, name, role));
	}

	public SessionInfo unregister(String sessionId) {
		if (sessionId == null) return null;
		return sessions.remove(sessionId);
	}

	public boolean isSessionOf(String sessionId, String roomNumber, String name) {
		if (sessionId == null) return false;
		SessionInfo info = sessions.get(sessionId);
		return info != null
				&& info.getRole() == Role.PARTICIPANT
				&& Objects.equals(info.getRoomNumber(), roomNumber)
				&& Objects.equals(info.getName(), name);
	}

	public boolean isOnline(String roomNumber, String name) {
		return sessions.values().stream().anyMatch(info -> info.getRole() == Role.PARTICIPANT
				&& Objects.equals(info.getRoomNumber(), roomNumber)
				&& Objects.equals(info.getName(), name));
	}

	public boolean isExaminerOnline(String roomNumber) {
		return sessions.values().stream().anyMatch(info -> info.getRole() == Role.EXAMINER
				&& Objects.equals(info.getRoomNumber(), roomNumber));
	}

	public Set<String> onlineParticipants(String roomNumber) {
		return sessions.values().stream()
				.filter(info -> info.getRole() == Role.PARTICIPANT && Objects.equals(info.getRoomNumber(), roomNumber))
				.map(SessionInfo::getName)
				.collect(Collectors.toSet());
	}
}

package kr.or.uknowhat.api.ubusiness.common;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import kr.or.uknowhat.api.ubusiness.jwt.JwtFilter;
import kr.or.uknowhat.api.ubusiness.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * STOMP CONNECT 프레임의 Authorization 헤더(JWT)로 출제자를 인증한다.
 * 참여자는 토큰 없이 접속하며, 출제자 전용 명령(START/NEXT 등)은 인증된 방 생성자만 실행할 수 있다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class StompAuthChannelInterceptor implements ChannelInterceptor {

	private final JwtProvider jwtProvider;

	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
		if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
			String bearer = accessor.getFirstNativeHeader(JwtFilter.AUTHORIZATION_HEADER);
			if (StringUtils.hasText(bearer) && bearer.startsWith(JwtFilter.BEARER_PREFIX)) {
				String jwt = bearer.substring(JwtFilter.BEARER_PREFIX.length());
				try {
					if (jwtProvider.validateToken(jwt)) {
						accessor.setUser(jwtProvider.getAuthentication(jwt));
					}
				} catch (Exception e) {
					log.info("STOMP 인증 토큰 처리 실패: {}", e.getMessage());
				}
			}
		}
		return message;
	}
}

package kr.or.uknowhat.api.ubusiness.util;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SecurityUtil {
	private SecurityUtil() { }
	
	// SecurityContext 에 유저 정보가 저장되는 시점
    // Request 가 들어올 때 JwtFilter 의 doFilter 에서 저장
	public static Optional<String> getCurrentUserId() {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if (authentication == null || authentication.getName() == null) {
			log.debug("Security Context에 인증 정보가 없습니다.");
			return Optional.empty();
		}
		
		String userId = null;
		if (authentication.getPrincipal() instanceof UserDetails) {
			UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
			userId = springSecurityUser.getUsername();
		} else if (authentication.getPrincipal() instanceof String) {
			userId = (String) authentication.getPrincipal();
		}
		
		return Optional.ofNullable(userId);
	}
}

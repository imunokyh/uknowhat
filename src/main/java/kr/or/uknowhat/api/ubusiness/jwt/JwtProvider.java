package kr.or.uknowhat.api.ubusiness.jwt;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import kr.or.uknowhat.api.ubusiness.question.vo.JwtVo;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtProvider implements InitializingBean {
	
	private static final String AUTHORITIES_KEY = "auth";

	private final String secret;
	private final long tokenValidityInMilliseconds;
	
	private Key key;

	public JwtProvider(@Value("${jwt.secret}") String secret,
					   @Value("${jwt.token-validity-in-seconds}") long tokenValidityInSeconds) {
		this.secret = secret;
		this.tokenValidityInMilliseconds = tokenValidityInSeconds * 1000;
	}
	
	@Override
	public void afterPropertiesSet() {
		byte[] keyBytes = Decoders.BASE64.decode(this.secret);
		this.key = Keys.hmacShaKeyFor(keyBytes);
	}

	public JwtVo createToken(Authentication authentication) {
		// 권한들 가져오기
		String authorities = authentication.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.joining(","));

		long now = (new Date()).getTime();
		Date validity = new Date(now + this.tokenValidityInMilliseconds);

		// Access Token 생성
		String token = Jwts.builder()
				.setSubject(authentication.getName()) 		// payload "sub" : "name"
				.claim(AUTHORITIES_KEY, authorities) 		// payload "auth": "ROLE_USER"
				.setExpiration(validity) 					// payload "exp" : 1516239022 (예시)
				.signWith(key, SignatureAlgorithm.HS512)	// header  "alg" : "HS512"
				.compact();
		
		return JwtVo.builder()
				.token(token)
				.build();
	}

	public Authentication getAuthentication(String token) {
		// 토큰 복호화
		Claims claims = parseClaims(token);

		// 클레임에서 권한 정보 가져오기
		Collection<? extends GrantedAuthority> authorities = 
				Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());

		// UserDetails 객체를 만들어서 Authentication 리턴
		UserDetails principal = new User(claims.getSubject(), "", authorities);

		return new UsernamePasswordAuthenticationToken(principal, "", authorities);
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder()
			 .setSigningKey(key)
			 .build()
			 .parseClaimsJws(token);
			return true;
		} catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
			log.info("잘못된 JWT 서명입니다.");
		} catch (ExpiredJwtException e) {
			log.info("만료된 JWT 토큰입니다.");
		} catch (UnsupportedJwtException e) {
			log.info("지원되지 않는 JWT 토큰입니다.");
		} catch (IllegalArgumentException e) {
			log.info("JWT 토큰이 잘못되었습니다.");
		}
		return false;
	}
	
	private Claims parseClaims(String token) {
		try {
			return Jwts.parserBuilder()
		            .setSigningKey(key)
		            .build()
		            .parseClaimsJws(token)
		            .getBody();
		} catch (ExpiredJwtException e) {
			return e.getClaims();
		}
	}
}

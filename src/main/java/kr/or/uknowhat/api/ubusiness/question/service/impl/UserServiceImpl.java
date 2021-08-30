package kr.or.uknowhat.api.ubusiness.question.service.impl;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.or.uknowhat.api.ubusiness.common.ErrorCode;
import kr.or.uknowhat.api.ubusiness.jwt.JwtProvider;
import kr.or.uknowhat.api.ubusiness.question.domain.User;
import kr.or.uknowhat.api.ubusiness.question.repositories.UserRepository;
import kr.or.uknowhat.api.ubusiness.question.service.UserService;
import kr.or.uknowhat.api.ubusiness.question.vo.JwtVo;
import kr.or.uknowhat.api.ubusiness.question.vo.LoginVo;
import kr.or.uknowhat.api.ubusiness.question.vo.UserVo;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private AuthenticationManagerBuilder authenticationManagerBuilder;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private JwtProvider jwtProvider;
	
	@Override
	public Page<User> listUser(int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("userId").descending());
		return userRepository.findAll(pageable);
	}

	@Override
	public User getUser(String userId) {
		User user = null;
		
		Optional<User> optionalUser = userRepository.findByUserId(userId);
		if (optionalUser.isPresent()) {
			user = optionalUser.get();
		}
		
		return user;
	}

	@Override
	public int insertUser(UserVo userVo) {
		if (userRepository.existsByUserId(userVo.getId())) {
			return ErrorCode.ERROR.getCode();
		}
		else {
			User user = new User();
			user.setUserId(userVo.getId());
			user.setUserPassword(passwordEncoder.encode(userVo.getPassword()));
			user.setUserAuthority(userVo.getAuthority());
			userRepository.save(user);
		}
		
		return ErrorCode.SUCCESS.getCode();
	}

	@Override
	public int updateUser(UserVo userVo) {
		Optional<User> optionalUser = userRepository.findByUserId(userVo.getId());
		if (optionalUser.isPresent()) {
			User user = optionalUser.get();
			user.setUserPassword(passwordEncoder.encode(userVo.getPassword()));
			user.setUserAuthority(userVo.getAuthority());
			userRepository.save(user);
		}
		else {
			return ErrorCode.ERROR.getCode();
		}
		
		return ErrorCode.SUCCESS.getCode();
	}

	@Override
	public int deleteUser(String userId) {
		Optional<User> optionalUser = userRepository.findByUserId(userId);
		if (optionalUser.isPresent()) {
			userRepository.delete(optionalUser.get());
		}
		else {
			return ErrorCode.ERROR.getCode();
		}
		
		return ErrorCode.SUCCESS.getCode();
	}

	@Override
	public JwtVo loginUser(LoginVo loginVo) {
		// 1. Login ID/PW 를 기반으로 AuthenticationToken 생성
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginVo.getId(), loginVo.getPassword());
		
		// 2. 실제로 검증 (사용자 비밀번호 체크) 이 이루어지는 부분
        //    authenticate 메서드가 실행이 될 때 CustomUserDetailsServiceImpl 에서 만들었던 loadUserByUsername 메서드가 실행됨
		try {
			Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			
			Optional<User> optionalUser = userRepository.findByUserId(loginVo.getId());
			if (optionalUser.isPresent()) {
				User user = optionalUser.get();
				user.setLastAccessDate(new Date());
				userRepository.save(user);
			}
			
			// 3. 인증 정보를 기반으로 JWT 토큰 생성/발급
	        return jwtProvider.createToken(authentication);
		} catch (BadCredentialsException e) {
			log.debug("비밀번호가 틀렸습니다.");
		}
		
		return null;
	}
}

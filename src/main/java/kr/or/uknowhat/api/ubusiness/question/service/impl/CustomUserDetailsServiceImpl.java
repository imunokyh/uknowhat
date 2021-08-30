package kr.or.uknowhat.api.ubusiness.question.service.impl;



import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.or.uknowhat.api.ubusiness.question.domain.User;
import kr.or.uknowhat.api.ubusiness.question.repositories.UserRepository;

@Service
public class CustomUserDetailsServiceImpl implements UserDetailsService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepository.findByUserId(username)
				.map(this::createUserDetail)
				.orElseThrow(() -> new UsernameNotFoundException(username + "을 찾을 수 없습니다."));
	}
	
	private UserDetails createUserDetail(User user) {
		GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(user.getUserAuthority().toString());
		
		return new org.springframework.security.core.userdetails.User(
				String.valueOf(user.getUserId()),
				user.getUserPassword(),
				Collections.singleton(grantedAuthority)
		);
	}

}

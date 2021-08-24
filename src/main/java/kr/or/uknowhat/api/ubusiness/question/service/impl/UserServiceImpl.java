package kr.or.uknowhat.api.ubusiness.question.service.impl;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import kr.or.uknowhat.api.ubusiness.question.domain.User;
import kr.or.uknowhat.api.ubusiness.question.repositories.UserRepository;
import kr.or.uknowhat.api.ubusiness.question.service.UserService;
import kr.or.uknowhat.api.ubusiness.question.vo.UserVo;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Override
	public Page<User> listUser(int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
		return userRepository.findAll(pageable);
	}

	@Override
	public User getUser(String id) {
		User user = null;
		
		Optional<User> optionalUser = userRepository.findById(id);
		if (optionalUser.isPresent()) {
			user = optionalUser.get();
		}
		
		return user;
	}

	@Override
	public void insertUser(UserVo userVo) {
		User user = new User();
		user.setId(userVo.getId());
		user.setPassword(userVo.getPassword());
		user.setAuthority(userVo.getAuthority());
		userRepository.save(user);
	}

	@Override
	public void updateUser(UserVo userVo) {
		Optional<User> optionalUser = userRepository.findById(userVo.getId());
		if (optionalUser.isPresent()) {
			User user = optionalUser.get();
			user.setPassword(userVo.getPassword());
			user.setAuthority(userVo.getAuthority());
			userRepository.save(user);
		}
	}

	@Override
	public void deleteUser(String id) {
		Optional<User> optionalUser = userRepository.findById(id);
		if (optionalUser.isPresent()) {
			userRepository.delete(optionalUser.get());
		}
	}
}

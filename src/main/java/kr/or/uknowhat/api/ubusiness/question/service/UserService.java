package kr.or.uknowhat.api.ubusiness.question.service;

import org.springframework.data.domain.Page;

import kr.or.uknowhat.api.ubusiness.question.domain.User;
import kr.or.uknowhat.api.ubusiness.question.vo.UserVo;

public interface UserService {
	Page<User> listUser(int page, int size);
	User getUser(String id);
	void insertUser(UserVo userVo);
	void updateUser(UserVo userVo);
	void deleteUser(String id);
}

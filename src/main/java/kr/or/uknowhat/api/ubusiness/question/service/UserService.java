package kr.or.uknowhat.api.ubusiness.question.service;

import org.springframework.data.domain.Page;

import kr.or.uknowhat.api.ubusiness.question.domain.User;
import kr.or.uknowhat.api.ubusiness.question.vo.JwtVo;
import kr.or.uknowhat.api.ubusiness.question.vo.LoginVo;
import kr.or.uknowhat.api.ubusiness.question.vo.UserVo;

public interface UserService {
	Page<User> listUser(int page, int size);
	User getUser(String userId);
	int insertUser(UserVo userVo);
	int updateUser(UserVo userVo);
	int deleteUser(String userId);
	JwtVo loginUser(LoginVo loginVo);
}

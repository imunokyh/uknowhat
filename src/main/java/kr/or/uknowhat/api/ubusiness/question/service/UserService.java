package kr.or.uknowhat.api.ubusiness.question.service;

import java.util.List;

import kr.or.uknowhat.api.ubusiness.question.domain.User;

public interface UserService {
	List<User> listUser() throws Exception;
}

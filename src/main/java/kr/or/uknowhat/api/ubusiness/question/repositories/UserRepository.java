package kr.or.uknowhat.api.ubusiness.question.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.or.uknowhat.api.ubusiness.question.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
	List<User> findAll();
}

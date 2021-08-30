package kr.or.uknowhat.api.ubusiness.question.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.or.uknowhat.api.ubusiness.question.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
	Page<User> findAll(Pageable pageable);
	Optional<User> findByUserId(String userId);
	boolean existsByUserId(String userId);
}

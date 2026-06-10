package com.hr24.employee.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hr24.employee.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	
	Optional<User> findByLoginId(String loginId);

}
package com.hr24.employee.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hr24.employee.entity.User;
import com.hr24.employee.enums.UserStatus;

public interface UserRepository extends JpaRepository<User, Long> {
	
	Optional<User> findByLoginId(String loginId);
	
	boolean existsByDepartment_DepartmentIdAndStatusIn(
	        Long departmentId,
	        List<UserStatus> statuses
	);
	
	@Query("""
	    select u
	    from User u
	    left join fetch u.department
	    left join fetch u.position
	    where u.loginId = :loginId
	""")
	Optional<User> findByLoginIdWithDepartmentAndPosition(
	        @Param("loginId") String loginId
	);

}
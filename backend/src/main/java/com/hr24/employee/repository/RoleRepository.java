package com.hr24.employee.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hr24.employee.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

	List<Role> findByRoleIdIn(List<Long> roleIds);
}

package com.hr24.employee.repository;

import java.lang.invoke.StringConcatFactory;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hr24.employee.entity.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

	boolean existsByDepartmentCodeIgnoreCase(String departmentCode);

	boolean existsByDepartmentNameIgnoreCase(String departmentName);

	boolean existsByDepartmentNameIgnoreCaseAndDepartmentIdNot(String departmentName, Long departmentId);

	boolean existsByParentDepartment_DepartmentIdAndIsActive(Long parentDepartmentId, String isActive);

	@Query("""
				select d
				from Department d
				left join fetch d.parentDepartment
				order by d.departmentId
			""")
	List<Department> findAllWithParentDepartment();

}

package com.hr24.employee.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hr24.employee.entity.User;
import com.hr24.employee.enums.EmploymentType;
import com.hr24.employee.enums.UserStatus;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByLoginId(String loginId);

	boolean existsByDepartment_DepartmentIdAndStatusIn(Long departmentId, List<UserStatus> statuses);

	boolean existsByPosition_PositionIdAndStatusIn(Long positionId, List<UserStatus> statuses);

	@Query("""
			    select u
			    from User u
			    left join fetch u.department
			    left join fetch u.position
			    where u.loginId = :loginId
			""")
	Optional<User> findByLoginIdWithDepartmentAndPosition(@Param("loginId") String loginId);

	// 직원 목록 조회
	@Query("""
			SELECT u
			FROM User u
			LEFT JOIN FETCH u.department d
			LEFT JOIN FETCH u.position p
			WHERE (:departmentId IS NULL OR d.departmentId = :departmentId)
			  AND (:status IS NULL OR u.status = :status)
			  AND (:employmentType IS NULL OR u.employmentType = :employmentType)
			  AND (
			        :keyword IS NULL
			        OR LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
			        OR LOWER(u.employeeNo) LIKE LOWER(CONCAT('%', :keyword, '%'))
			        OR LOWER(u.loginId) LIKE LOWER(CONCAT('%', :keyword, '%'))
			  )
			ORDER BY u.name ASC, u.employeeId ASC
			""")
	List<User> searchHrEmployees(@Param("departmentId") Long departmentId, @Param("status") UserStatus status,
			@Param("employmentType") EmploymentType employmentType, @Param("keyword") String keyword);

	// 직원 상세 조회
	@Query("""
			SELECT u
			FROM User u
			LEFT JOIN FETCH u.department d
			LEFT JOIN FETCH u.position p
			WHERE u.employeeId = :employeeId
			""")
	Optional<User> findByEmployeeIdWithDepartmentAndPosition(@Param("employeeId") Long employeeId);

	// 전체 ACTIVE 직원 수 삭제 예정
	long countByStatus(UserStatus status);

	// 부서별 직접 소속 ACTIVE 직원 수 삭제 예정
	long countByDepartment_DepartmentIdAndStatus(Long departmentId, UserStatus status);

	// 고용형태별 ACTIVE 직원 수
	long countByStatusAndEmploymentType(UserStatus status, EmploymentType employmentType);

	// 부서별 직접 소속 ACTIVE + 고용형태 기준 직원 수
	long countByDepartment_DepartmentIdAndStatusAndEmploymentType(Long departmentId, UserStatus status,
			EmploymentType employmentType);
	
	// 로그인 ID 중복 체크
	boolean existsByLoginIdIgnoreCase(String loginId);

	// 이메일 중복 체크
	boolean existsByEmailIgnoreCase(String email);
	
	// 이메일 중복 체크 - 본인 제외
	boolean existsByEmailIgnoreCaseAndEmployeeIdNot(String email, Long employeeId);

	// 직급 sort_order 기준 팀장급 이상 ACTIVE 직원 조회 (팀장 회의 알림용)
	@Query("SELECT u FROM User u WHERE u.position.sortOrder >= :minSortOrder AND u.status = :status")
	List<User> findActiveManagersByMinSortOrder(@Param("minSortOrder") Integer minSortOrder, @Param("status") UserStatus status);

	// 사번 중복 체크
	boolean existsByEmployeeNo(String employeeNo);

	// 해당 연도 사번 중 가장 마지막 사번 조회
	// 예: prefix = "EMP2026"
	// EMP20260009가 가장 크면 이 값을 가져와서 다음 사번 EMP20260010 생성
	Optional<User> findTopByEmployeeNoStartingWithOrderByEmployeeNoDesc(String prefix);
}
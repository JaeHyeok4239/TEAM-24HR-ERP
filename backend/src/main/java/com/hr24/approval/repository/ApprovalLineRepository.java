package com.hr24.approval.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hr24.approval.entity.ApprovalLine;
import com.hr24.document.entity.DocumentType;
import com.hr24.employee.entity.Department;

public interface ApprovalLineRepository extends JpaRepository<ApprovalLine, Long>{
	
	// 문서타입 + 부서별 결재선
	List<ApprovalLine> findByDocumentTypeAndDepartmentOrderByStepOrderAsc(
	    DocumentType documentType, Department department);

	// 문서타입 + 공통 결재선 (부서 null)
	List<ApprovalLine> findByDocumentTypeAndDepartmentIsNullOrderByStepOrderAsc(
	    DocumentType documentType);
	
	//문서 종류 + 부서
	List<ApprovalLine> findByDepartmentAndDocumentTypeOrderByStepOrderAsc(
	        Department department,
	        DocumentType documentType
	);
	
	//내 부서로 조회
	
	//검색(유형 + 부서 + 키워드)
	@Query("""
		    select al
		    from ApprovalLine al
		    where (:departmentId is null
		           or al.department.departmentId = :departmentId)
		      and (:documentType is null
		           or al.documentType.typeId = :documentType)
		      and (:keyword is null
		           or al.approver.name like concat('%', :keyword, '%'))
		    order by al.department.departmentId,
		             al.documentType.typeId,
		             al.stepOrder
		""")
		List<ApprovalLine> search(
		        @Param("departmentId") Long departmentId,
		        @Param("documentType") Long documentType,
		        @Param("keyword") String keyword
		);
	
	//중복 확인
	boolean existsByDocumentTypeAndDepartmentAndStepOrder(
		    DocumentType documentType,
		    Department department,
		    Integer stepOrder
		);
}

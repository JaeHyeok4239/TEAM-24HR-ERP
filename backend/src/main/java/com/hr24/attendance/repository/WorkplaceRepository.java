package com.hr24.attendance.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hr24.attendance.entity.Workplace;
import com.hr24.attendance.enums.WorkplaceCode;

public interface WorkplaceRepository extends JpaRepository<Workplace, Long>{
	Optional<Workplace> findByWorkplaceCode(WorkplaceCode workplaceCode);

	List<Workplace> findByWorkplaceCodeIn(Set<WorkplaceCode> workplaceCodes);
}

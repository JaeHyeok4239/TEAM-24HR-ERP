package com.hr24.attendance.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hr24.attendance.entity.Workplace;

public interface WorkplaceRepository extends JpaRepository<Workplace, Long>{
    Optional<Workplace> findByWorkplaceCode(String workplaceCode);

    // 필터링용 메서드
    List<Workplace> findByWorkplaceCodeInAndWorkplaceCodeStartingWith(Collection<String> codes, String prefix);
}

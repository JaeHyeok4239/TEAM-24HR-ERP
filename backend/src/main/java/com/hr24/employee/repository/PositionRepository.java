package com.hr24.employee.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hr24.employee.entity.Position;

public interface PositionRepository extends JpaRepository<Position, Long> {

    List<Position> findAllByOrderBySortOrderAscPositionIdAsc();

    boolean existsByPositionCodeIgnoreCase(String positionCode);

    boolean existsByPositionNameIgnoreCase(String positionName);

    boolean existsByPositionNameIgnoreCaseAndPositionIdNot(
            String positionName,
            Long positionId
    );
}
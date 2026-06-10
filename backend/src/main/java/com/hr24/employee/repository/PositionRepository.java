package com.hr24.employee.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hr24.employee.entity.Position;

public interface PositionRepository extends JpaRepository<Position, Long> {

}
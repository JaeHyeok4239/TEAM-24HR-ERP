package com.hr24.evaluation.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hr24.evaluation.entity.PromotionRule;

public interface PromotionRuleRepository extends JpaRepository<PromotionRule, Long> {

    List<PromotionRule> findAllByIsActiveOrderByPromotionRuleIdAsc(String isActive);

    Optional<PromotionRule> findByFromPosition_PositionIdAndIsActive(
            Long fromPositionId,
            String isActive
    );
}
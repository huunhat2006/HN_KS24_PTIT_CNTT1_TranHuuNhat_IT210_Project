package com.restaurant.hnks24cntt1it210tranhuunhatproject.repository;

import com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.AcademicEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AcademicEvaluationRepository extends JpaRepository<AcademicEvaluation, Integer> {

	Optional<AcademicEvaluation> findBySession_Id(Integer sessionId);
}
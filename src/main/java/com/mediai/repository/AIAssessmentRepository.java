package com.mediai.repository;

import com.mediai.entity.AIAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AIAssessmentRepository extends JpaRepository<AIAssessment, Long> {
    List<AIAssessment> findByPatientIdOrderByCreatedAtDesc(Long patientId);
    List<AIAssessment> findAllByOrderByCreatedAtDesc();
    long countByIsDoctorReviewed(boolean reviewed);
}

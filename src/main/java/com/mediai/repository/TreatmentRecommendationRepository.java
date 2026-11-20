package com.mediai.repository;

import com.mediai.entity.TreatmentRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TreatmentRecommendationRepository extends JpaRepository<TreatmentRecommendation, Long> {
    List<TreatmentRecommendation> findByPatientIdOrderByCreatedAtDesc(Long patientId);
    List<TreatmentRecommendation> findByDoctorIdOrderByCreatedAtDesc(Long doctorId);
    List<TreatmentRecommendation> findAllByOrderByCreatedAtDesc();
}

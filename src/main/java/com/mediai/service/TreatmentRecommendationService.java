package com.mediai.service;

import com.mediai.entity.TreatmentRecommendation;
import com.mediai.repository.TreatmentRecommendationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TreatmentRecommendationService {

    private final TreatmentRecommendationRepository recommendationRepository;

    public List<TreatmentRecommendation> getAllRecommendations() {
        return recommendationRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<TreatmentRecommendation> getRecommendationsForPatient(Long patientId) {
        return recommendationRepository.findByPatientIdOrderByCreatedAtDesc(patientId);
    }

    public List<TreatmentRecommendation> getRecommendationsForDoctor(Long doctorId) {
        return recommendationRepository.findByDoctorIdOrderByCreatedAtDesc(doctorId);
    }

    public Optional<TreatmentRecommendation> getById(Long id) {
        return recommendationRepository.findById(id);
    }

    @Transactional
    public boolean approveRecommendation(Long id, String doctorNotes) {
        return recommendationRepository.findById(id).map(r -> {
            r.setStatus(TreatmentRecommendation.Status.APPROVED);
            r.setApprovedAt(LocalDateTime.now());
            if (doctorNotes != null && !doctorNotes.trim().isEmpty()) {
                r.setDoctorNotes(doctorNotes.trim());
            }
            recommendationRepository.save(r);
            return true;
        }).orElse(false);
    }
}

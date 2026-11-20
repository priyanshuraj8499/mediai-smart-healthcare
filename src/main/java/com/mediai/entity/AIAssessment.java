package com.mediai.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ai_assessments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AIAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(name = "patient_name", nullable = false, length = 150)
    private String patientName;

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false, length = 20)
    private String gender;

    @Column(nullable = false, length = 1500)
    private String symptoms;

    @Column(name = "symptom_duration", nullable = false, length = 100)
    private String symptomDuration;

    @Column(name = "medical_history", length = 800)
    private String medicalHistory;

    @Column(name = "existing_conditions", length = 800)
    private String existingConditions;

    @Column(name = "additional_notes", length = 800)
    private String additionalNotes;

    @Column(name = "possible_conditions_json", columnDefinition = "TEXT")
    private String possibleConditionsJson;

    @Column(name = "confidence_level", length = 30)
    private String confidenceLevel; // High, Moderate, Low

    @Column(name = "suggested_next_steps_json", columnDefinition = "TEXT")
    private String suggestedNextStepsJson;

    @Column(name = "general_guidance_json", columnDefinition = "TEXT")
    private String generalGuidanceJson;

    @Column(name = "red_flags_json", columnDefinition = "TEXT")
    private String redFlagsJson;

    @Column(name = "is_disclaimer_acknowledged")
    @Builder.Default
    private Boolean isDisclaimerAcknowledged = true;

    @Column(name = "is_doctor_reviewed")
    @Builder.Default
    private Boolean isDoctorReviewed = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewing_doctor_id")
    private Doctor reviewingDoctor;

    @Column(name = "doctor_feedback", length = 1500)
    private String doctorFeedback;

    @Column(name = "is_live_ai")
    @Builder.Default
    private Boolean isLiveAi = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.isDoctorReviewed == null) this.isDoctorReviewed = false;
        if (this.isDisclaimerAcknowledged == null) this.isDisclaimerAcknowledged = true;
    }

    public Boolean getReviewedByDoctor() {
        return Boolean.TRUE.equals(isDoctorReviewed);
    }

    public String getDoctorNotes() {
        return doctorFeedback;
    }

    public String getReportedSymptoms() {
        return symptoms;
    }

    public String getPrimaryCondition() {
        if (possibleConditionsJson != null && !possibleConditionsJson.isEmpty()) {
            return possibleConditionsJson;
        }
        return "Clinical Assessment Pending";
    }

    public String getUrgencyLevel() {
        if (confidenceLevel != null && confidenceLevel.equalsIgnoreCase("High")) {
            return "URGENT";
        }
        return "ROUTINE";
    }

    public Double getConfidenceScore() {
        if ("High".equalsIgnoreCase(confidenceLevel)) return 0.92;
        if ("Moderate".equalsIgnoreCase(confidenceLevel)) return 0.85;
        return 0.75;
    }

    public String getReasoning() {
        if (generalGuidanceJson != null && !generalGuidanceJson.isEmpty()) {
            return generalGuidanceJson;
        }
        return "Multi-symptom clinical evaluation based on reported intake parameters.";
    }

    public String getSuggestedActions() {
        if (suggestedNextStepsJson != null && !suggestedNextStepsJson.isEmpty()) {
            return suggestedNextStepsJson;
        }
        return "Schedule consultation with an attending medical specialist.";
    }
}

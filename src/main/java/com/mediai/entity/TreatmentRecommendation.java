package com.mediai.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "treatment_recommendations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TreatmentRecommendation {

    public enum Status {
        DRAFT,
        APPROVED,
        MODIFIED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_id")
    private AIAssessment assessment;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(name = "patient_summary", length = 1000)
    private String patientSummary;

    @Column(name = "preliminary_assessment", length = 1000)
    private String preliminaryAssessment;

    @Column(name = "suggested_care_plan_json", columnDefinition = "TEXT")
    private String suggestedCarePlanJson;

    @Column(name = "precautions_json", columnDefinition = "TEXT")
    private String precautionsJson;

    @Column(name = "suggested_follow_up", length = 500)
    private String suggestedFollowUp;

    @Column(name = "doctor_notes", length = 1500)
    private String doctorNotes;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private Status status = Status.DRAFT;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) this.status = Status.DRAFT;
    }

    public String getTitle() {
        return preliminaryAssessment != null ? preliminaryAssessment : "Clinical Care Protocol";
    }

    public String getDiagnosisCondition() {
        return patientSummary != null ? patientSummary : "Primary Condition";
    }

    public Boolean getApprovedByDoctor() {
        return status == Status.APPROVED;
    }

    public String getLifestyleModifications() {
        return suggestedCarePlanJson != null ? suggestedCarePlanJson : "Standard recovery protocol.";
    }

    public String getMedicationClasses() {
        return precautionsJson != null ? precautionsJson : "First-line therapeutics with monitoring.";
    }

    public String getFollowUpSchedule() {
        return suggestedFollowUp != null ? suggestedFollowUp : "Outpatient follow-up in 14 days.";
    }
}

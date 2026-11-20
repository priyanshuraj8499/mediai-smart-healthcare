package com.mediai.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vital_readings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VitalReading {

    public enum Status {
        NORMAL,
        WARNING,
        CRITICAL
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(name = "heart_rate", nullable = false)
    private Integer heartRate;

    @Column(name = "systolic_bp", nullable = false)
    private Integer systolicBp;

    @Column(name = "diastolic_bp", nullable = false)
    private Integer diastolicBp;

    @Column(name = "body_temperature", nullable = false)
    private Double bodyTemperature;

    @Column(name = "oxygen_saturation", nullable = false)
    private Integer oxygenSaturation;

    @Column(name = "respiratory_rate")
    private Integer respiratoryRate;

    @Column(name = "blood_glucose")
    private Double bloodGlucose;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private Status status = Status.NORMAL;

    @Column(length = 500)
    private String notes;

    @Column(name = "is_simulated")
    @Builder.Default
    private Boolean isSimulated = true;

    @Column(name = "recorded_at")
    private LocalDateTime recordedAt;

    @PrePersist
    protected void onCreate() {
        if (this.recordedAt == null) {
            this.recordedAt = LocalDateTime.now();
        }
    }

    public Integer getBloodPressureSystolic() {
        return systolicBp;
    }

    public Integer getBloodPressureDiastolic() {
        return diastolicBp;
    }

    public LocalDateTime getReadingTime() {
        return recordedAt != null ? recordedAt : LocalDateTime.now();
    }
}

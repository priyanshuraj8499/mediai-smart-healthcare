package com.mediai.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "patients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(length = 20)
    private String gender; // Male, Female, Other

    @Column(name = "blood_group", length = 10)
    private String bloodGroup; // A+, O+, B+, AB+, etc.

    @Column(name = "emergency_contact", length = 150)
    private String emergencyContact;

    @Column(length = 300)
    private String address;

    @Column(length = 300)
    private String allergies; // Comma-separated: Penicillin, Sulfa drugs

    @Column(name = "chronic_conditions", length = 300)
    private String chronicConditions; // Comma-separated: Hypertension, Type-2 Diabetes

    @Column(name = "medical_history_summary", length = 1000)
    private String medicalHistorySummary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_doctor_id")
    private Doctor assignedDoctor;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public String getFullName() {
        return user != null ? user.getFullName() : "Patient";
    }

    public String getEmail() {
        return user != null ? user.getEmail() : "";
    }

    public String getPhone() {
        return user != null ? user.getPhone() : "";
    }

    public String getEmergencyContactName() {
        return emergencyContact != null ? emergencyContact : "Next of Kin";
    }

    public String getEmergencyContactPhone() {
        return user != null && user.getPhone() != null ? user.getPhone() : "+1 (555) 234-5678";
    }
}

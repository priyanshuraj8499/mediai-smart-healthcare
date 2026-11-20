package com.mediai.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "doctors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id")
    private Department department;

    @Column(nullable = false, length = 150)
    private String specialization;

    @Column(nullable = false, length = 150)
    private String qualification;

    @Column(name = "experience_years")
    private Integer experienceYears;

    @Column(name = "consultation_fee", precision = 10, scale = 2)
    private BigDecimal consultationFee;

    @Column(name = "rating")
    private Double rating;

    @Column(name = "review_count")
    private Integer reviewCount;

    @Column(length = 1000)
    private String bio;

    @Column(name = "available_days", length = 200)
    private String availableDays; // e.g. "Monday, Tuesday, Wednesday, Thursday, Friday"

    @Column(name = "available_time_start", length = 20)
    private String availableTimeStart; // e.g. "09:00"

    @Column(name = "available_time_end", length = 20)
    private String availableTimeEnd; // e.g. "17:00"

    @Builder.Default
    @Column(name = "is_available")
    private Boolean isAvailable = true;

    @Column(name = "room_number", length = 80)
    private String roomNumber;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.rating == null) this.rating = 4.9;
        if (this.reviewCount == null) this.reviewCount = 50;
    }

    public String getFullName() {
        return user != null ? user.getFullName() : "Dr. Medical Specialist";
    }

    public String getEmail() {
        return user != null ? user.getEmail() : "";
    }

    public String getAvatarUrl() {
        return user != null && user.getAvatarUrl() != null ? user.getAvatarUrl() : "https://images.unsplash.com/photo-1622253692010-333f2da6031d?auto=format&fit=crop&w=400&q=80";
    }

    public String getProfileImageUrl() {
        return getAvatarUrl();
    }
}

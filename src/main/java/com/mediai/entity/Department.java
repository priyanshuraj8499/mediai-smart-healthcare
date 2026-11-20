package com.mediai.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "departments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Column(length = 500)
    private String description;

    @Column(length = 50)
    private String icon;

    @Column(name = "head_doctor_name", length = 150)
    private String headDoctorName;

    @Column(name = "bed_capacity")
    private Integer bedCapacity;

    @Column(name = "occupied_beds")
    private Integer occupiedBeds;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.occupiedBeds == null) this.occupiedBeds = 0;
        if (this.bedCapacity == null) this.bedCapacity = 50;
    }
}

package com.mediai.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "hospital_resources")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HospitalResource {

    public enum Category {
        BEDS,
        ICU,
        VENTILATORS,
        AMBULANCES,
        OPERATING_ROOMS
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "resource_name", nullable = false, length = 120)
    private String resourceName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Category category;

    @Column(name = "total_count", nullable = false)
    private Integer totalCount;

    @Column(name = "available_count", nullable = false)
    private Integer availableCount;

    @Column(name = "occupied_count", nullable = false)
    private Integer occupiedCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @Column(name = "department_name", length = 100)
    private String departmentName;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @PrePersist
    @PreUpdate
    protected void onSave() {
        this.lastUpdated = LocalDateTime.now();
        if (this.totalCount != null && this.availableCount != null) {
            this.occupiedCount = Math.max(0, this.totalCount - this.availableCount);
        }
    }

    public Integer getTotalCapacity() {
        return totalCount != null ? totalCount : 0;
    }

    public String getLocation() {
        if (departmentName != null && !departmentName.isEmpty()) {
            return departmentName;
        }
        return "Central Facility";
    }

    public String getStatus() {
        if (availableCount == null || availableCount == 0) {
            return "FULL";
        }
        if (totalCount != null && ((double) availableCount / totalCount) < 0.25) {
            return "LIMITED";
        }
        return "AVAILABLE";
    }
}

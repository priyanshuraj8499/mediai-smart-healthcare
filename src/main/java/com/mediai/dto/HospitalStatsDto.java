package com.mediai.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HospitalStatsDto {
    private long totalPatients;
    private long totalDoctors;
    private long totalAppointments;
    private long pendingAppointments;
    private int availableBeds;
    private int totalBeds;
    private int emergencyCases;
    private int occupancyRate;
}

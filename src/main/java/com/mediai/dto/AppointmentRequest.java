package com.mediai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentRequest {

    @NotNull(message = "Please select a physician")
    private Long doctorId;

    @NotNull(message = "Appointment date is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate appointmentDate;

    @NotBlank(message = "Appointment time slot is required")
    private String appointmentTime;

    @NotBlank(message = "Please choose in-person or telehealth")
    private String meetingType;

    @NotBlank(message = "Please describe symptoms or reason for visit")
    private String symptoms;
}

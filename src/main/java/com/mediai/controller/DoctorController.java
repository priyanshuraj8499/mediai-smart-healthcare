package com.mediai.controller;

import com.mediai.entity.*;
import com.mediai.security.CustomUserDetails;
import com.mediai.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/doctor")
@PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;
    private final PatientService patientService;
    private final AppointmentService appointmentService;
    private final DiagnosisService diagnosisService;
    private final TreatmentRecommendationService treatmentService;
    private final MonitoringService monitoringService;

    private Doctor getCurrentDoctor(CustomUserDetails userDetails) {
        if (userDetails != null) {
            return doctorService.getDoctorByUserId(userDetails.getId())
                    .orElseGet(() -> doctorService.getAllDoctors().stream().findFirst().orElse(null));
        }
        return doctorService.getAllDoctors().stream().findFirst().orElse(null);
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Doctor doctor = getCurrentDoctor(userDetails);
        if (doctor == null) {
            return "redirect:/";
        }

        List<Patient> patients = patientService.getPatientsByDoctor(doctor.getId());
        List<Appointment> appointments = appointmentService.getAppointmentsForDoctor(doctor.getId());
        List<AIAssessment> assessments = diagnosisService.getAllAssessments();
        List<TreatmentRecommendation> recommendations = treatmentService.getRecommendationsForDoctor(doctor.getId());

        model.addAttribute("doctor", doctor);
        model.addAttribute("patients", patients.isEmpty() ? patientService.getAllPatients() : patients);
        model.addAttribute("appointments", appointments);
        model.addAttribute("assessments", assessments);
        model.addAttribute("recommendations", recommendations);

        long pendingCount = appointments.stream().filter(a -> a.getStatus() == Appointment.Status.PENDING).count();
        long confirmedCount = appointments.stream().filter(a -> a.getStatus() == Appointment.Status.APPROVED).count();
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("confirmedCount", confirmedCount);

        return "doctor/doctor-dashboard";
    }

    @GetMapping("/appointments")
    public String doctorAppointments(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Doctor doctor = getCurrentDoctor(userDetails);
        List<Appointment> appointments = doctor != null ?
                appointmentService.getAppointmentsForDoctor(doctor.getId()) : Collections.emptyList();
        model.addAttribute("doctor", doctor);
        model.addAttribute("appointments", appointments);
        return "doctor/doctor-appointments";
    }

    @PostMapping("/appointments/{id}/status")
    public String updateAppointmentStatus(
            @PathVariable("id") Long id,
            @RequestParam("status") Appointment.Status status,
            @RequestParam(value = "doctorNotes", required = false) String doctorNotes,
            RedirectAttributes redirectAttributes
    ) {
        boolean ok = appointmentService.updateStatus(id, status, doctorNotes);
        if (ok) {
            redirectAttributes.addFlashAttribute("successMessage", "Appointment updated to " + status + ".");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update appointment.");
        }
        return "redirect:/doctor/appointments";
    }

    @GetMapping("/patients")
    public String doctorPatients(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Doctor doctor = getCurrentDoctor(userDetails);
        List<Patient> patients = doctor != null ? patientService.getPatientsByDoctor(doctor.getId()) : Collections.emptyList();
        model.addAttribute("doctor", doctor);
        model.addAttribute("patients", patients.isEmpty() ? patientService.getAllPatients() : patients);
        return "doctor/doctor-patients";
    }

    @GetMapping("/records")
    public String doctorRecords(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Doctor doctor = getCurrentDoctor(userDetails);
        model.addAttribute("doctor", doctor);
        model.addAttribute("assessments", diagnosisService.getAllAssessments());
        model.addAttribute("recommendations", treatmentService.getAllRecommendations());
        return "doctor/doctor-records";
    }

    @GetMapping("/profile")
    public String doctorProfile(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Doctor doctor = getCurrentDoctor(userDetails);
        model.addAttribute("doctor", doctor);
        return "doctor/doctor-profile";
    }

    @PostMapping("/assessments/{id}/review")
    public String reviewAssessment(
            @PathVariable("id") Long id,
            @RequestParam("feedback") String feedback,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes
    ) {
        Doctor doctor = getCurrentDoctor(userDetails);
        Long docId = doctor != null ? doctor.getId() : 1L;
        boolean ok = diagnosisService.reviewAssessment(id, docId, feedback);
        if (ok) {
            redirectAttributes.addFlashAttribute("successMessage", "Clinical assessment review submitted successfully.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Review submission failed.");
        }
        return "redirect:/doctor/dashboard";
    }

    @GetMapping("/treatment-recommendations")
    public String treatmentRecommendations(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Doctor doctor = getCurrentDoctor(userDetails);
        model.addAttribute("doctor", doctor);
        model.addAttribute("recommendations", treatmentService.getAllRecommendations());
        return "doctor/treatment-recommendations";
    }

    @PostMapping("/treatment-recommendations/{id}/approve")
    public String approveTreatment(
            @PathVariable("id") Long id,
            @RequestParam(value = "doctorNotes", required = false) String doctorNotes,
            RedirectAttributes redirectAttributes
    ) {
        boolean ok = treatmentService.approveRecommendation(id, doctorNotes);
        if (ok) {
            redirectAttributes.addFlashAttribute("successMessage", "Clinical care plan authorized and signed off.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Approval failed.");
        }
        return "redirect:/doctor/treatment-recommendations";
    }
}

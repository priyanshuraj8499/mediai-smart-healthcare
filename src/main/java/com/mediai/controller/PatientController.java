package com.mediai.controller;

import com.mediai.dto.AppointmentRequest;
import com.mediai.dto.DiagnosisRequest;
import com.mediai.entity.*;
import com.mediai.security.CustomUserDetails;
import com.mediai.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/patient")
@PreAuthorize("hasAnyRole('PATIENT', 'ADMIN')")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;
    private final DoctorService doctorService;
    private final DepartmentService departmentService;
    private final AppointmentService appointmentService;
    private final MonitoringService monitoringService;
    private final DiagnosisService diagnosisService;
    private final TreatmentRecommendationService treatmentService;

    private Patient getCurrentPatient(CustomUserDetails userDetails) {
        if (userDetails != null) {
            return patientService.getPatientByUserId(userDetails.getId())
                    .orElseGet(() -> patientService.getAllPatients().stream().findFirst().orElse(null));
        }
        return patientService.getAllPatients().stream().findFirst().orElse(null);
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Patient patient = getCurrentPatient(userDetails);
        if (patient == null) {
            return "redirect:/";
        }

        List<Appointment> appointments = appointmentService.getAppointmentsForPatient(patient.getId());
        VitalReading latestVital = monitoringService.getLatestReading(patient.getId());
        List<AIAssessment> assessments = diagnosisService.getAssessmentsForPatient(patient.getId());
        List<TreatmentRecommendation> carePlans = treatmentService.getRecommendationsForPatient(patient.getId());

        model.addAttribute("patient", patient);
        model.addAttribute("appointments", appointments);
        model.addAttribute("latestVital", latestVital);
        model.addAttribute("assessments", assessments);
        model.addAttribute("carePlans", carePlans);
        model.addAttribute("assignedDoctor", patient.getAssignedDoctor());

        return "patient/patient-dashboard";
    }

    @GetMapping("/book-appointment")
    public String showBookAppointmentForm(
            @RequestParam(value = "doctorId", required = false) Long doctorId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model
    ) {
        Patient patient = getCurrentPatient(userDetails);
        model.addAttribute("patient", patient);
        model.addAttribute("doctors", doctorService.getAllDoctors());
        model.addAttribute("departments", departmentService.getAllDepartments());

        if (!model.containsAttribute("appointmentRequest")) {
            AppointmentRequest req = AppointmentRequest.builder()
                    .doctorId(doctorId != null ? doctorId : (doctorService.getAllDoctors().isEmpty() ? null : doctorService.getAllDoctors().get(0).getId()))
                    .appointmentDate(LocalDate.now().plusDays(2))
                    .appointmentTime("10:00")
                    .meetingType("in-person")
                    .build();
            model.addAttribute("appointmentRequest", req);
        }

        return "patient/book-appointment";
    }

    @PostMapping("/book-appointment")
    public String bookAppointment(
            @Valid @ModelAttribute("appointmentRequest") AppointmentRequest request,
            BindingResult bindingResult,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        Patient patient = getCurrentPatient(userDetails);
        if (bindingResult.hasErrors()) {
            model.addAttribute("patient", patient);
            model.addAttribute("doctors", doctorService.getAllDoctors());
            model.addAttribute("departments", departmentService.getAllDepartments());
            return "patient/book-appointment";
        }

        try {
            appointmentService.bookAppointment(patient.getId(), request);
            redirectAttributes.addFlashAttribute("successMessage", "Appointment booked successfully with double-booking validation verified!");
            return "redirect:/patient/appointments";
        } catch (IllegalStateException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("patient", patient);
            model.addAttribute("doctors", doctorService.getAllDoctors());
            model.addAttribute("departments", departmentService.getAllDepartments());
            return "patient/book-appointment";
        }
    }

    @GetMapping("/appointments")
    public String patientAppointments(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Patient patient = getCurrentPatient(userDetails);
        List<Appointment> appointments = patient != null ?
                appointmentService.getAppointmentsForPatient(patient.getId()) : Collections.emptyList();
        model.addAttribute("patient", patient);
        model.addAttribute("appointments", appointments);
        return "patient/patient-appointments";
    }

    @PostMapping("/appointments/{id}/cancel")
    public String cancelAppointment(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes
    ) {
        Patient patient = getCurrentPatient(userDetails);
        appointmentService.cancelAppointment(id, patient != null ? patient.getId() : null);
        redirectAttributes.addFlashAttribute("successMessage", "Appointment cancelled successfully.");
        return "redirect:/patient/appointments";
    }

    @GetMapping("/monitoring")
    public String patientMonitoring(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Patient patient = getCurrentPatient(userDetails);
        if (patient == null) return "redirect:/";

        List<VitalReading> history = monitoringService.getVitalHistory(patient.getId());
        VitalReading latest = monitoringService.getLatestReading(patient.getId());
        List<VitalReading> recentLogs = monitoringService.getRecentLogs(patient.getId());

        model.addAttribute("patient", patient);
        model.addAttribute("latestVital", latest);
        model.addAttribute("vitalHistory", history);
        model.addAttribute("recentLogs", recentLogs);
        return "patient/patient-monitoring";
    }

    @PostMapping("/monitoring/simulate")
    public String simulateVitals(
            @RequestParam(value = "induceAnomaly", defaultValue = "false") boolean induceAnomaly,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes
    ) {
        Patient patient = getCurrentPatient(userDetails);
        if (patient != null) {
            VitalReading reading = monitoringService.simulateNextTick(patient.getId(), induceAnomaly);
            if (induceAnomaly) {
                redirectAttributes.addFlashAttribute("errorMessage", "Simulated Anomaly Triggered: " + reading.getStatus() + " alert logged!");
            } else {
                redirectAttributes.addFlashAttribute("successMessage", "New telemetry sample generated and logged.");
            }
        }
        return "redirect:/patient/monitoring";
    }

    @GetMapping("/medical-history")
    public String medicalHistory(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Patient patient = getCurrentPatient(userDetails);
        model.addAttribute("patient", patient);
        return "patient/patient-medical-history";
    }

    @GetMapping("/diagnosis")
    public String patientDiagnosis(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Patient patient = getCurrentPatient(userDetails);
        model.addAttribute("patient", patient);
        model.addAttribute("assessments", diagnosisService.getAssessmentsForPatient(patient != null ? patient.getId() : 1L));

        if (!model.containsAttribute("diagnosisRequest")) {
            model.addAttribute("diagnosisRequest", DiagnosisRequest.builder()
                    .patientName(patient != null ? patient.getFullName() : "Alexander Hayes")
                    .age(35)
                    .gender(patient != null ? patient.getGender() : "Male")
                    .symptomDuration("3 days, intermittent")
                    .medicalHistory(patient != null ? patient.getMedicalHistorySummary() : "")
                    .existingConditions(patient != null ? patient.getChronicConditions() : "")
                    .build());
        }

        return "patient/patient-diagnosis";
    }

    @PostMapping("/diagnosis")
    public String submitDiagnosis(
            @Valid @ModelAttribute("diagnosisRequest") DiagnosisRequest request,
            BindingResult bindingResult,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        Patient patient = getCurrentPatient(userDetails);
        if (bindingResult.hasErrors()) {
            model.addAttribute("patient", patient);
            model.addAttribute("assessments", diagnosisService.getAssessmentsForPatient(patient != null ? patient.getId() : 1L));
            return "patient/patient-diagnosis";
        }

        AIAssessment asst = diagnosisService.performAssessment(patient.getId(), request);
        redirectAttributes.addFlashAttribute("newAssessment", asst);
        redirectAttributes.addFlashAttribute("successMessage", "AI Preliminary assessment generated successfully.");
        return "redirect:/patient/diagnosis";
    }
}

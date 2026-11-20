package com.mediai.controller;

import com.mediai.dto.HospitalStatsDto;
import com.mediai.entity.Appointment;
import com.mediai.entity.Department;
import com.mediai.entity.Doctor;
import com.mediai.entity.HospitalResource;
import com.mediai.entity.Patient;
import com.mediai.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final HospitalResourceService resourceService;
    private final DoctorService doctorService;
    private final PatientService patientService;
    private final AppointmentService appointmentService;
    private final DepartmentService departmentService;
    private final NotificationService notificationService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        HospitalStatsDto stats = resourceService.getHospitalStats();
        List<HospitalResource> resources = resourceService.getAllResources();
        List<Appointment> recentAppointments = appointmentService.getAllAppointments();
        List<Doctor> doctors = doctorService.getAllDoctors();
        List<Department> departments = departmentService.getAllDepartments();

        model.addAttribute("stats", stats);
        model.addAttribute("resources", resources);
        model.addAttribute("appointments", recentAppointments.stream().limit(6).toList());
        model.addAttribute("doctors", doctors);
        model.addAttribute("departments", departments);
        return "admin/admin-dashboard";
    }

    @GetMapping("/doctors")
    public String manageDoctors(Model model) {
        model.addAttribute("doctors", doctorService.getAllDoctors());
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "admin/manage-doctors";
    }

    @GetMapping("/patients")
    public String managePatients(Model model) {
        model.addAttribute("patients", patientService.getAllPatients());
        return "admin/manage-patients";
    }

    @GetMapping("/appointments")
    public String manageAppointments(Model model) {
        model.addAttribute("appointments", appointmentService.getAllAppointments());
        return "admin/manage-appointments";
    }

    @GetMapping("/departments")
    public String manageDepartments(Model model) {
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "admin/manage-departments";
    }

    @GetMapping("/resources")
    public String manageResources(Model model) {
        model.addAttribute("resources", resourceService.getAllResources());
        model.addAttribute("stats", resourceService.getHospitalStats());
        return "admin/manage-resources";
    }

    @PostMapping("/resources/{id}/adjust")
    public String adjustResource(
            @PathVariable("id") Long id,
            @RequestParam("availableCount") int availableCount,
            RedirectAttributes redirectAttributes
    ) {
        boolean ok = resourceService.updateAvailableCount(id, availableCount);
        if (ok) {
            redirectAttributes.addFlashAttribute("successMessage", "Resource availability updated successfully.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Resource update failed.");
        }
        return "redirect:/admin/resources";
    }
}

package com.mediai.controller;

import com.mediai.dto.HospitalStatsDto;
import com.mediai.entity.Department;
import com.mediai.entity.Doctor;
import com.mediai.service.DepartmentService;
import com.mediai.service.DoctorService;
import com.mediai.service.HospitalResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final DoctorService doctorService;
    private final DepartmentService departmentService;
    private final HospitalResourceService hospitalResourceService;

    @GetMapping("/")
    public String index(Model model) {
        HospitalStatsDto stats = hospitalResourceService.getHospitalStats();
        List<Doctor> featuredDoctors = doctorService.getAllDoctors();
        List<Department> departments = departmentService.getAllDepartments();

        model.addAttribute("stats", stats);
        model.addAttribute("doctors", featuredDoctors);
        model.addAttribute("departments", departments);
        return "index";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("doctors", doctorService.getAllDoctors());
        return "about";
    }

    @GetMapping("/features")
    public String features(Model model) {
        return "features";
    }

    @GetMapping("/doctors")
    public String doctors(
            @RequestParam(value = "departmentId", required = false) Long departmentId,
            @RequestParam(value = "query", required = false) String query,
            Model model
    ) {
        List<Doctor> doctors;
        if (query != null && !query.trim().isEmpty()) {
            doctors = doctorService.searchDoctors(query);
        } else if (departmentId != null && departmentId > 0) {
            doctors = doctorService.getDoctorsByDepartment(departmentId);
        } else {
            doctors = doctorService.getAllDoctors();
        }

        model.addAttribute("doctors", doctors);
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("selectedDept", departmentId != null ? departmentId : 0L);
        model.addAttribute("query", query != null ? query : "");
        return "doctors";
    }

    @GetMapping("/contact")
    public String contact(Model model) {
        return "contact";
    }
}

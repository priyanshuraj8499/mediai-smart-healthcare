package com.mediai.controller;

import com.mediai.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class AppointmentController {

    @GetMapping("/appointments")
    public String appointments(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails != null) {
            String role = userDetails.getRoleName();
            if ("ROLE_ADMIN".equals(role)) {
                return "redirect:/admin/appointments";
            } else if ("ROLE_DOCTOR".equals(role)) {
                return "redirect:/doctor/appointments";
            } else {
                return "redirect:/patient/appointments";
            }
        }
        return "redirect:/login";
    }
}

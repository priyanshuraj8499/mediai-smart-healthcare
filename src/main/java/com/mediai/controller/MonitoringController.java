package com.mediai.controller;

import com.mediai.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MonitoringController {

    @GetMapping("/monitoring")
    public String monitoring(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails != null) {
            String role = userDetails.getRoleName();
            if ("ROLE_DOCTOR".equals(role)) {
                return "redirect:/doctor/patients";
            } else if ("ROLE_ADMIN".equals(role)) {
                return "redirect:/admin/resources";
            } else {
                return "redirect:/patient/monitoring";
            }
        }
        return "redirect:/login";
    }
}

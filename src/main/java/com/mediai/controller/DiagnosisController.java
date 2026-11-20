package com.mediai.controller;

import com.mediai.dto.DiagnosisRequest;
import com.mediai.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DiagnosisController {

    @GetMapping({"/ai-diagnosis", "/diagnosis"})
    public String diagnosis(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails != null) {
            String role = userDetails.getRoleName();
            if ("ROLE_DOCTOR".equals(role)) {
                return "redirect:/doctor/records";
            } else if ("ROLE_ADMIN".equals(role)) {
                return "redirect:/admin/dashboard";
            } else {
                return "redirect:/patient/diagnosis";
            }
        }
        return "redirect:/login";
    }
}

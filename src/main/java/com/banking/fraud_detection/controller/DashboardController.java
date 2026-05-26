package com.banking.fraud_detection.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication) {
        String role = authentication.getAuthorities()
            .iterator().next().getAuthority();

        if (role.equals("ROLE_ADMIN")) {
            return "redirect:/admin/dashboard";
        } else if (role.equals("ROLE_CUSTOMER")) {
            return "redirect:/customer/dashboard";
        } else if (role.equals("ROLE_FRAUD_ANALYST")) {
            return "redirect:/analyst/dashboard";
        }
        return "redirect:/login";
    }
}
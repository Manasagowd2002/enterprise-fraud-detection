package com.banking.fraud_detection.controller;

import com.banking.fraud_detection.entity.User;
import com.banking.fraud_detection.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @ModelAttribute User user,
            RedirectAttributes redirectAttributes) {

        if (userService.isEmailAlreadyRegistered(user.getEmail())) {
            redirectAttributes.addFlashAttribute(
                "error", "Email already registered. Please use a different email.");
            return "redirect:/register";
        }

        userService.registerUser(user);
        redirectAttributes.addFlashAttribute(
            "success", "Registration successful! Please wait for admin approval.");
        return "redirect:/login";
    }

    
}
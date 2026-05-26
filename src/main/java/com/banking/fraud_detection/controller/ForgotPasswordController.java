package com.banking.fraud_detection.controller;

import com.banking.fraud_detection.entity.User;
import com.banking.fraud_detection.entity.UserStatus;
import com.banking.fraud_detection.service.EmailService;
import com.banking.fraud_detection.service.OtpService;
import com.banking.fraud_detection.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Optional;

@Controller
public class ForgotPasswordController {

    @Autowired
    private UserService userService;

    @Autowired
    private OtpService otpService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(
            @RequestParam String email,
            RedirectAttributes redirectAttributes) {

        // Step 1 - Validate email format
        if (!isValidEmail(email)) {
            redirectAttributes.addFlashAttribute(
                "error",
                "Please enter a valid email address " +
                "(example: user@example.com)");
            return "redirect:/forgot-password";
        }

        // Step 2 - Check if email exists in DB
        Optional<User> userOpt =
            userService.findByEmail(email);
        if (userOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute(
                "error",
                "No account found with this email address.");
            return "redirect:/forgot-password";
        }

        // Step 3 - Check if account is ACTIVE
        User user = userOpt.get();
        if (!user.getStatus().equals(UserStatus.ACTIVE)) {
            redirectAttributes.addFlashAttribute(
                "error",
                "Account is not eligible for password reset. " +
                "Status: " + user.getStatus());
            return "redirect:/forgot-password";
        }

        // Step 4 - Generate OTP and send email
        String otp = otpService.generateOtp(email);
        emailService.sendOtpEmail(email, otp);

        redirectAttributes.addFlashAttribute(
            "success",
            "OTP sent to your registered email. " +
            "Valid for 5 minutes.");
        redirectAttributes.addFlashAttribute(
            "email", email);
        return "redirect:/verify-otp";
    }

    @GetMapping("/verify-otp")
    public String verifyOtpPage(Model model) {
        return "verify-otp";
    }

    @PostMapping("/verify-otp")
    public String processVerifyOtp(
            @RequestParam String email,
            @RequestParam String otp,
            RedirectAttributes redirectAttributes) {

        // Check if OTP expired
        if (otpService.isExpired(email)) {
            redirectAttributes.addFlashAttribute(
                "error",
                "OTP expired. Please request a new OTP.");
            redirectAttributes.addFlashAttribute(
                "email", email);
            return "redirect:/verify-otp";
        }

        // Check if OTP is correct
        if (!otpService.validateOtp(email, otp)) {
            redirectAttributes.addFlashAttribute(
                "error", "Invalid OTP. Please try again.");
            redirectAttributes.addFlashAttribute(
                "email", email);
            return "redirect:/verify-otp";
        }

        // OTP is valid - allow password reset
        redirectAttributes.addFlashAttribute(
            "email", email);
        return "redirect:/reset-password";
    }

    @GetMapping("/reset-password")
    public String resetPasswordPage() {
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(
            @RequestParam String email,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            RedirectAttributes redirectAttributes) {

        // Check passwords match
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute(
                "error", "Passwords do not match.");
            redirectAttributes.addFlashAttribute(
                "email", email);
            return "redirect:/reset-password";
        }

        // Check password strength
        if (!isStrongPassword(newPassword)) {
            redirectAttributes.addFlashAttribute(
                "error",
                "Password must contain 8+ characters, " +
                "uppercase, lowercase, number and " +
                "special character.");
            redirectAttributes.addFlashAttribute(
                "email", email);
            return "redirect:/reset-password";
        }

        // Update password in DB
        Optional<User> userOpt =
            userService.findByEmail(email);
        userOpt.ifPresent(user -> {
            user.setPassword(
                passwordEncoder.encode(newPassword));
            userService.saveUser(user);
        });

        // Clear OTP
        otpService.clearOtp(email);

        redirectAttributes.addFlashAttribute(
            "success",
            "Password reset successful! " +
            "Please login with your new password.");
        return "redirect:/login";
    }

    private boolean isValidEmail(String email) {
        return email != null
            && email.matches(
                "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+" +
                "\\.[a-zA-Z]{2,}$")
            && !email.contains(" ");
    }

    private boolean isStrongPassword(String password) {
        return password.length() >= 8
            && password.matches(".*[A-Z].*")
            && password.matches(".*[a-z].*")
            && password.matches(".*[0-9].*")
            && password.matches(
                ".*[!@#$%^&*(),.?\":{}|<>].*");
    }
}
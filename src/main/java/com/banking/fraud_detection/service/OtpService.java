package com.banking.fraud_detection.service;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class OtpService {

    private final Map<String, String> otpStore
        = new HashMap<>();
    private final Map<String, LocalDateTime> otpExpiry
        = new HashMap<>();

    public String generateOtp(String email) {
        String otp = String.format("%06d",
            new Random().nextInt(999999));
        otpStore.put(email, otp);
        otpExpiry.put(email,
            LocalDateTime.now().plusMinutes(5));
        return otp;
    }

    public boolean validateOtp(String email, String otp) {
        if (!otpStore.containsKey(email)) {
            return false;
        }
        if (LocalDateTime.now()
                .isAfter(otpExpiry.get(email))) {
            otpStore.remove(email);
            otpExpiry.remove(email);
            return false;
        }
        return otpStore.get(email).equals(otp);
    }

    public boolean isExpired(String email) {
        if (!otpExpiry.containsKey(email)) {
            return true;
        }
        return LocalDateTime.now()
            .isAfter(otpExpiry.get(email));
    }

    public void clearOtp(String email) {
        otpStore.remove(email);
        otpExpiry.remove(email);
    }
}
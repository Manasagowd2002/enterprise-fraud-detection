package com.banking.fraud_detection.service;

import com.banking.fraud_detection.entity.Role;
import com.banking.fraud_detection.entity.User;
import com.banking.fraud_detection.entity.UserStatus;
import com.banking.fraud_detection.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean isEmailAlreadyRegistered(String email) {
        return userRepository.existsByEmail(email);
    }

    public User registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus(UserStatus.PENDING);
        user.setRole(Role.CUSTOMER);
        return userRepository.save(user);
    }

    public List<User> getPendingUsers() {
        return userRepository.findByStatus(UserStatus.PENDING);
    }

    public List<User> getActiveUsers() {
        return userRepository.findByStatus(UserStatus.ACTIVE);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public void approveUser(Long id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setStatus(UserStatus.ACTIVE);
            userRepository.save(user);
        });
    }

    public void rejectUser(Long id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setStatus(UserStatus.REJECTED);
            userRepository.save(user);
        });
    }

    public void freezeUser(Long id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setStatus(UserStatus.FROZEN);
            userRepository.save(user);
        });
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    public User saveUser(User user) {
        return userRepository.save(user);
    }
}
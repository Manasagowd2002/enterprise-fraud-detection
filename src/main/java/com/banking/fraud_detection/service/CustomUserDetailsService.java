package com.banking.fraud_detection.service;

import com.banking.fraud_detection.entity.User;
import com.banking.fraud_detection.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
            .orElseThrow(() ->
                new UsernameNotFoundException(
                    "User not found with email: " + email));

        if (!user.getStatus().name().equals("ACTIVE")) {
            throw new UsernameNotFoundException(
                "Account is not active. Status: " + user.getStatus());
        }

        return new org.springframework.security.core.userdetails.User(
            user.getEmail(),
            user.getPassword(),
            Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
            )
        );
    }
}
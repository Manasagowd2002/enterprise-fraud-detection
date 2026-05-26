package com.banking.fraud_detection.repository;

import com.banking.fraud_detection.entity.User;
import com.banking.fraud_detection.entity.UserStatus;
import com.banking.fraud_detection.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByStatus(UserStatus status);

    List<User> findByRole(Role role);
}
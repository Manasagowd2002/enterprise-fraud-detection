package com.banking.fraud_detection.repository;

import com.banking.fraud_detection.entity.Transaction;
import com.banking.fraud_detection.entity.TransactionStatus;
import com.banking.fraud_detection.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findBySender(User sender);

    List<Transaction> findByStatus(TransactionStatus status);

    List<Transaction> findBySenderOrderByCreatedAtDesc(User sender);

    List<Transaction> findByStatusOrderByCreatedAtDesc(TransactionStatus status);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.sender = :sender " +
           "AND t.createdAt >= :since")
    int countRecentTransactions(User sender, LocalDateTime since);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "WHERE t.sender = :sender AND t.createdAt >= :since")
    BigDecimal sumAmountSince(User sender, LocalDateTime since);

    @Query("SELECT COALESCE(AVG(t.amount), 0) FROM Transaction t " +
           "WHERE t.sender = :sender")
    BigDecimal averageTransactionAmount(User sender);

    List<Transaction> findBySenderAndReceiverAccountAndAmount(
        User sender, String receiverAccount, BigDecimal amount);
}
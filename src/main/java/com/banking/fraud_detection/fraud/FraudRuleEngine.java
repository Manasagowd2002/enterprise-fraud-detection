package com.banking.fraud_detection.fraud;

import com.banking.fraud_detection.entity.Transaction;
import com.banking.fraud_detection.entity.TransactionStatus;
import com.banking.fraud_detection.entity.User;
import com.banking.fraud_detection.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class FraudRuleEngine {

    @Autowired
    private TransactionRepository transactionRepository;

    private static final BigDecimal SUSPICIOUS_AMOUNT
        = new BigDecimal("50000");
    private static final BigDecimal CRITICAL_AMOUNT
        = new BigDecimal("200000");
    private static final BigDecimal DAILY_LIMIT
        = new BigDecimal("500000");
    private static final int VELOCITY_LIMIT = 5;
    private static final int VELOCITY_MINUTES = 60;
    private static final BigDecimal ANOMALY_MULTIPLIER
        = new BigDecimal("5");

    private static final List<String> BLACKLISTED_ACCOUNTS
        = List.of(
            "9999999999",
            "1111111111",
            "0000000000"
        );

    public Transaction evaluate(Transaction transaction,
            User sender) {

        int riskScore = 0;
        List<String> reasons = new ArrayList<>();

        BigDecimal amount = transaction.getAmount();

        // Rule 1 — Amount band detection
        if (amount.compareTo(CRITICAL_AMOUNT) >= 0) {
            riskScore += 40;
            reasons.add("Critical amount: Rs." + amount);
        } else if (amount.compareTo(SUSPICIOUS_AMOUNT) >= 0) {
            riskScore += 20;
            reasons.add("Suspicious amount: Rs." + amount);
        }

        // Rule 2 — Velocity check
        LocalDateTime since = LocalDateTime.now()
            .minusMinutes(VELOCITY_MINUTES);
        int recentCount = transactionRepository
            .countRecentTransactions(sender, since);
        if (recentCount >= VELOCITY_LIMIT) {
            riskScore += 25;
            reasons.add("High velocity: " + recentCount
                + " transactions in 60 min");
        }

        // Rule 3 — Duplicate detection
        List<Transaction> duplicates = transactionRepository
            .findBySenderAndReceiverAccountAndAmount(
                sender,
                transaction.getReceiverAccount(),
                transaction.getAmount());
        if (!duplicates.isEmpty()) {
            riskScore += 20;
            reasons.add("Duplicate transaction detected");
        }

        // Rule 4 — Daily limit check
        LocalDateTime startOfDay = LocalDateTime.now()
            .toLocalDate().atStartOfDay();
        BigDecimal dailyTotal = transactionRepository
            .sumAmountSince(sender, startOfDay);
        if (dailyTotal.add(amount)
                .compareTo(DAILY_LIMIT) > 0) {
            riskScore += 30;
            reasons.add("Daily limit exceeded: Rs."
                + dailyTotal.add(amount));
        }

        // Rule 5 — Historical anomaly
        BigDecimal avgAmount = transactionRepository
            .averageTransactionAmount(sender);
        if (avgAmount.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal threshold = avgAmount
                .multiply(ANOMALY_MULTIPLIER);
            if (amount.compareTo(threshold) > 0) {
                riskScore += 20;
                reasons.add("Amount anomaly: Rs." + amount
                    + " vs avg Rs." + avgAmount);
            }
        }

        // Rule 6 — Blacklist check
        if (BLACKLISTED_ACCOUNTS.contains(
                transaction.getReceiverAccount())) {
            riskScore += 50;
            reasons.add("Blacklisted receiver: "
                + transaction.getReceiverAccount());
        }

        // Set risk score and reason
        transaction.setRiskScore(riskScore);
        if (!reasons.isEmpty()) {
            transaction.setFlagReason(
                String.join(" | ", reasons));
        }

        // Make decision
        if (riskScore >= 81) {
            transaction.setStatus(
                TransactionStatus.BLOCKED);
        } else if (riskScore >= 61) {
            transaction.setStatus(
                TransactionStatus.UNDER_REVIEW);
        } else if (riskScore >= 31) {
            transaction.setStatus(
                TransactionStatus.FLAGGED);
        } else {
            transaction.setStatus(
                TransactionStatus.APPROVED);
        }
        
        System.out.println("FRAUD ENGINE: Score=" + riskScore + " Status=" + transaction.getStatus());
        return transaction;
    }
}
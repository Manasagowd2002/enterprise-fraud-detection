package com.banking.fraud_detection.service;

import com.banking.fraud_detection.entity.Transaction;
import com.banking.fraud_detection.entity.TransactionStatus;
import com.banking.fraud_detection.entity.User;
import com.banking.fraud_detection.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import com.banking.fraud_detection.fraud.FraudRuleEngine;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    public Transaction submitTransaction(Transaction transaction,
            User sender, FraudRuleEngine fraudRuleEngine) {
        transaction.setSender(sender);
        transaction = fraudRuleEngine.evaluate(
            transaction, sender);
        return transactionRepository.save(transaction);
    }

    public List<Transaction> getTransactionsByUser(User user) {
        return transactionRepository
            .findBySenderOrderByCreatedAtDesc(user);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public List<Transaction> getTransactionsByStatus(
            TransactionStatus status) {
        return transactionRepository
            .findByStatusOrderByCreatedAtDesc(status);
    }

    public Optional<Transaction> findById(Long id) {
        return transactionRepository.findById(id);
    }

    public Transaction save(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public int countRecentTransactions(User user,
            LocalDateTime since) {
        return transactionRepository
            .countRecentTransactions(user, since);
    }

    public BigDecimal sumAmountSince(User user,
            LocalDateTime since) {
        return transactionRepository
            .sumAmountSince(user, since);
    }

    public BigDecimal averageTransactionAmount(User user) {
        return transactionRepository
            .averageTransactionAmount(user);
    }

    public List<Transaction> findDuplicates(User user,
            String receiverAccount, BigDecimal amount) {
        return transactionRepository
            .findBySenderAndReceiverAccountAndAmount(
                user, receiverAccount, amount);
    }

    public long countFlaggedTransactions() {
        return transactionRepository
            .findByStatus(TransactionStatus.FLAGGED).size();
    }

    public long countPendingTransactions() {
        return transactionRepository
            .findByStatus(TransactionStatus.PENDING).size();
    }
}
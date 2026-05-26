package com.banking.fraud_detection.controller;

import com.banking.fraud_detection.entity.Transaction;
import com.banking.fraud_detection.entity.User;
import com.banking.fraud_detection.fraud.FraudRuleEngine;
import com.banking.fraud_detection.service.TransactionService;
import com.banking.fraud_detection.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.banking.fraud_detection.entity.Transaction;
import com.banking.fraud_detection.entity.TransactionStatus;
import java.util.List;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserService userService;

    @Autowired
    private FraudRuleEngine fraudRuleEngine;

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication,
            Model model) {
        User user = getCurrentUser(authentication);
        List<Transaction> transactions =
            transactionService.getTransactionsByUser(user);

        long totalCount = transactions.size();
        long pendingCount = transactions.stream()
            .filter(t -> t.getStatus() ==
                TransactionStatus.PENDING)
            .count();
        long approvedCount = transactions.stream()
            .filter(t -> t.getStatus() ==
                TransactionStatus.APPROVED)
            .count();
        long flaggedCount = transactions.stream()
            .filter(t -> t.getStatus() ==
                TransactionStatus.FLAGGED ||
                t.getStatus() ==
                TransactionStatus.BLOCKED)
            .count();

        model.addAttribute("user", user);
        model.addAttribute("transactions", transactions);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("approvedCount", approvedCount);
        model.addAttribute("flaggedCount", flaggedCount);
        return "customer/dashboard";
    }

    @GetMapping("/transaction/new")
    public String newTransactionPage(Model model) {
        model.addAttribute("transaction", new Transaction());
        return "customer/new-transaction";
    }

    @PostMapping("/transaction/submit")
    public String submitTransaction(
            @ModelAttribute Transaction transaction,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        User user = getCurrentUser(authentication);
        transactionService.submitTransaction(
            transaction, user, fraudRuleEngine);
        redirectAttributes.addFlashAttribute("success",
            "Transaction submitted successfully!");
        return "redirect:/customer/transaction/history";
    }

    @GetMapping("/transaction/history")
    public String transactionHistory(
            Authentication authentication, Model model) {
        User user = getCurrentUser(authentication);
        model.addAttribute("transactions",
            transactionService.getTransactionsByUser(user));
        return "customer/transaction-history";
    }

    private User getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        return userService.findByEmail(email).orElseThrow();
    }
}
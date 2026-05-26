package com.banking.fraud_detection.controller;

import com.banking.fraud_detection.entity.Transaction;
import com.banking.fraud_detection.entity.TransactionStatus;
import com.banking.fraud_detection.service.TransactionService;
import com.banking.fraud_detection.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("pendingCount",
            userService.getPendingUsers().size());
        model.addAttribute("activeCount",
            userService.getActiveUsers().size());
        model.addAttribute("flaggedCount",
            transactionService.countFlaggedTransactions());
        model.addAttribute("pendingTxCount",
            transactionService.countPendingTransactions());
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String manageUsers(Model model) {
        model.addAttribute("pendingUsers",
            userService.getPendingUsers());
        model.addAttribute("allUsers",
            userService.getAllUsers());
        return "admin/users";
    }

    @PostMapping("/users/approve/{id}")
    public String approveUser(@PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        userService.approveUser(id);
        redirectAttributes.addFlashAttribute(
            "success", "User approved successfully!");
        return "redirect:/admin/users";
    }

    @PostMapping("/users/reject/{id}")
    public String rejectUser(@PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        userService.rejectUser(id);
        redirectAttributes.addFlashAttribute(
            "success", "User rejected.");
        return "redirect:/admin/users";
    }

    @PostMapping("/users/freeze/{id}")
    public String freezeUser(@PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        userService.freezeUser(id);
        redirectAttributes.addFlashAttribute(
            "success", "User account frozen.");
        return "redirect:/admin/users";
    }

    @GetMapping("/transactions")
    public String manageTransactions(Model model) {
        model.addAttribute("flaggedTransactions",
            transactionService.getTransactionsByStatus(
                TransactionStatus.FLAGGED));
        model.addAttribute("underReviewTransactions",
            transactionService.getTransactionsByStatus(
                TransactionStatus.UNDER_REVIEW));
        model.addAttribute("allTransactions",
            transactionService.getAllTransactions());
        return "admin/transactions";
    }

    @PostMapping("/transactions/approve/{id}")
    public String approveTransaction(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        Optional<Transaction> txOpt =
            transactionService.findById(id);
        txOpt.ifPresent(tx -> {
            tx.setStatus(TransactionStatus.APPROVED);
            transactionService.save(tx);
        });
        redirectAttributes.addFlashAttribute(
            "success", "Transaction approved!");
        return "redirect:/admin/transactions";
    }

    @PostMapping("/transactions/reject/{id}")
    public String rejectTransaction(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        Optional<Transaction> txOpt =
            transactionService.findById(id);
        txOpt.ifPresent(tx -> {
            tx.setStatus(TransactionStatus.BLOCKED);
            transactionService.save(tx);
        });
        redirectAttributes.addFlashAttribute(
            "success", "Transaction blocked!");
        return "redirect:/admin/transactions";
    }
}
package com.finance.manager.controller;

import com.finance.manager.dto.TransactionRequest;
import com.finance.manager.dto.TransactionUpdateRequest;
import com.finance.manager.entity.Transaction;
import com.finance.manager.service.TransactionService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    private Long getUserIdOrThrow(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            throw new SecurityException("Unauthorized access");
        }
        return userId;
    }

    // Helper method to format a transaction exactly as the PDF requires
    private Map<String, Object> formatTransactionResponse(Transaction t) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", t.getId());
        map.put("amount", t.getAmount());
        map.put("date", t.getDate().toString());
        map.put("category", t.getCategory().getName());
        map.put("description", t.getDescription());
        map.put("type", t.getCategory().getType()); // Included from the Category entity
        return map;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createTransaction(@Valid @RequestBody TransactionRequest request, HttpSession session) {
        Long userId = getUserIdOrThrow(session);
        Transaction transaction = transactionService.createTransaction(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(formatTransactionResponse(transaction));
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getTransactions(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String category,
            HttpSession session) {
        
        Long userId = getUserIdOrThrow(session);
        List<Transaction> transactions = transactionService.getTransactions(userId, startDate, endDate, category);

        List<Map<String, Object>> formattedList = transactions.stream()
                .map(this::formatTransactionResponse)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("transactions", formattedList);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateTransaction(
            @PathVariable Long id, 
            @Valid @RequestBody TransactionUpdateRequest request, 
            HttpSession session) {
        
        Long userId = getUserIdOrThrow(session);
        Transaction transaction = transactionService.updateTransaction(id, request, userId);
        return ResponseEntity.ok(formatTransactionResponse(transaction));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteTransaction(@PathVariable Long id, HttpSession session) {
        Long userId = getUserIdOrThrow(session);
        transactionService.deleteTransaction(id, userId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Transaction deleted successfully");
        return ResponseEntity.ok(response);
    }
}
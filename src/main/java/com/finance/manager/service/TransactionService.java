package com.finance.manager.service;

import com.finance.manager.dto.TransactionRequest;
import com.finance.manager.entity.Category;
import com.finance.manager.entity.Transaction;
import com.finance.manager.entity.User;
import com.finance.manager.repository.CategoryRepository;
import com.finance.manager.repository.TransactionRepository;
import com.finance.manager.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public TransactionService(TransactionRepository transactionRepository, CategoryRepository categoryRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    // Helper method to find a category (checks both custom and default)
    private Category getValidCategoryForUser(String categoryName, Long userId) {
        return categoryRepository.findAllAvailableForUser(userId).stream()
                .filter(c -> c.getName().equalsIgnoreCase(categoryName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid category: " + categoryName));
    }

    public Transaction createTransaction(TransactionRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Category category = getValidCategoryForUser(request.getCategory(), userId);

        Transaction transaction = new Transaction(
                request.getAmount(),
                request.getDate(),
                request.getDescription(),
                category,
                user
        );

        return transactionRepository.save(transaction);
    }

    public List<Transaction> getTransactions(Long userId, LocalDate startDate, LocalDate endDate, String categoryName) {
        // Fetch all transactions for the user, already sorted newest first by the repository
        List<Transaction> transactions = transactionRepository.findByUserIdOrderByDateDesc(userId);

        // Apply filters if they were provided in the request
        return transactions.stream()
                .filter(t -> startDate == null || !t.getDate().isBefore(startDate))
                .filter(t -> endDate == null || !t.getDate().isAfter(endDate))
                .filter(t -> categoryName == null || t.getCategory().getName().equalsIgnoreCase(categoryName))
                .collect(Collectors.toList());
    }

    public Transaction updateTransaction(Long transactionId, TransactionRequest request, Long userId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

        // Ensure the transaction actually belongs to the logged-in user
        if (!transaction.getUser().getId().equals(userId)) {
            throw new SecurityException("Unauthorized access to this transaction");
        }

        Category category = getValidCategoryForUser(request.getCategory(), userId);

        // Update fields (Explicitly skipping the date field as per assignment rules)
        transaction.setAmount(request.getAmount());
        transaction.setCategory(category);
        transaction.setDescription(request.getDescription());

        return transactionRepository.save(transaction);
    }

    public void deleteTransaction(Long transactionId, Long userId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

        if (!transaction.getUser().getId().equals(userId)) {
            throw new SecurityException("Unauthorized access to this transaction");
        }

        transactionRepository.delete(transaction);
    }
}
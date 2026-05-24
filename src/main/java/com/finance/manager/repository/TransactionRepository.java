package com.finance.manager.repository;

import com.finance.manager.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    // Automatically generates a query to fetch a user's transactions, sorted by date descending
    List<Transaction> findByUserIdOrderByDateDesc(Long userId);
    
    // Checks if any transactions are tied to a specific category (needed for deletion constraints)
    boolean existsByCategoryId(Long categoryId);
}
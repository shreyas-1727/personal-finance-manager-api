package com.finance.manager.service;

import com.finance.manager.dto.TransactionRequest;
import com.finance.manager.entity.Category;
import com.finance.manager.entity.Transaction;
import com.finance.manager.entity.User;
import com.finance.manager.repository.CategoryRepository;
import com.finance.manager.repository.TransactionRepository;
import com.finance.manager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    // 1. Create fake versions of our database repositories
    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    // 2. Inject those fake repositories into the real TransactionService
    @InjectMocks
    private TransactionService transactionService;

    private User testUser;
    private Category testCategory;
    private TransactionRequest validRequest;

    // 3. Set up standard test data before each test runs
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);

        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Salary");
        testCategory.setType("INCOME");

        validRequest = new TransactionRequest();
        validRequest.setAmount(new BigDecimal("5000.00"));
        validRequest.setDate(LocalDate.now());
        validRequest.setCategory("Salary");
        validRequest.setDescription("Test Income");
    }

    @Test
    void createTransaction_Success() {
        // Arrange: Tell the fake repositories how to behave
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(categoryRepository.findAllAvailableForUser(1L)).thenReturn(List.of(testCategory));
        
        Transaction savedTransaction = new Transaction(
                validRequest.getAmount(), 
                validRequest.getDate(), 
                validRequest.getDescription(), 
                testCategory, 
                testUser
        );
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

        // Act: Call the real service method
        Transaction result = transactionService.createTransaction(validRequest, 1L);

        // Assert: Verify the result is exactly what we expect
        assertNotNull(result);
        assertEquals(new BigDecimal("5000.00"), result.getAmount());
        
        // Verify that the repository's save method was called exactly one time
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void createTransaction_UserNotFound_ThrowsException() {
        // Arrange: Simulate the database not finding the user
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert: Check that the proper exception is thrown
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.createTransaction(validRequest, 1L);
        });
        
        assertEquals("User not found", exception.getMessage());
        
        // Verify that we never tried to save a transaction to the database
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void getTransactions_Success() {
        // Arrange
        Transaction t1 = new Transaction(new BigDecimal("100.00"), LocalDate.now(), "Test", testCategory, testUser);
        when(transactionRepository.findByUserIdOrderByDateDesc(1L)).thenReturn(List.of(t1));

        // Act
        List<Transaction> result = transactionService.getTransactions(1L, null, null, null);

        // Assert
        assertEquals(1, result.size());
        verify(transactionRepository, times(1)).findByUserIdOrderByDateDesc(1L);
    }

    @Test
    void updateTransaction_Success() {
        // Arrange
        Transaction existingTransaction = new Transaction(new BigDecimal("100.00"), LocalDate.now(), "Old Desc", testCategory, testUser);
        existingTransaction.setId(1L);
        
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(existingTransaction));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(existingTransaction);

        com.finance.manager.dto.TransactionUpdateRequest updateRequest = new com.finance.manager.dto.TransactionUpdateRequest();
        updateRequest.setAmount(new BigDecimal("250.00"));

        // Act
        Transaction result = transactionService.updateTransaction(1L, updateRequest, 1L);

        // Assert
        assertEquals(new BigDecimal("250.00"), result.getAmount());
        verify(transactionRepository, times(1)).save(existingTransaction);
    }

    @Test
    void deleteTransaction_Success() {
        // Arrange
        Transaction existingTransaction = new Transaction(new BigDecimal("100.00"), LocalDate.now(), "Desc", testCategory, testUser);
        existingTransaction.setId(1L);
        
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(existingTransaction));

        // Act
        transactionService.deleteTransaction(1L, 1L);

        // Assert
        verify(transactionRepository, times(1)).delete(existingTransaction);
    }
}
package com.finance.manager.service;

import com.finance.manager.dto.GoalRequest;
import com.finance.manager.entity.Goal;
import com.finance.manager.entity.User;
import com.finance.manager.repository.GoalRepository;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoalServiceTest {

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private GoalService goalService;

    private User testUser;
    private Goal testGoal;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);

        testGoal = new Goal();
        testGoal.setId(1L);
        testGoal.setGoalName("Emergency Fund");
        testGoal.setTargetAmount(new BigDecimal("10000.00"));
        testGoal.setTargetDate(LocalDate.now().plusYears(1));
        testGoal.setStartDate(LocalDate.now());
        testGoal.setUser(testUser);
    }

    @Test
    void createGoal_Success() {
        // Arrange
        GoalRequest request = new GoalRequest();
        request.setGoalName("Emergency Fund");
        request.setTargetAmount(new BigDecimal("10000.00"));
        request.setTargetDate(LocalDate.now().plusYears(1));

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(goalRepository.save(any(Goal.class))).thenReturn(testGoal);

        // Act
        Goal result = goalService.createGoal(request, 1L);

        // Assert
        assertNotNull(result);
        assertEquals("Emergency Fund", result.getGoalName());
        verify(goalRepository, times(1)).save(any(Goal.class));
    }

    @Test
    void deleteGoal_Success() {
        // Arrange
        when(goalRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testGoal));

        // Act & Assert
        assertDoesNotThrow(() -> goalService.deleteGoal(1L, 1L));
        verify(goalRepository, times(1)).delete(testGoal);
    }

    @Test
    void deleteGoal_NotFound_ThrowsException() {
        // Arrange
        when(goalRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            goalService.deleteGoal(1L, 1L);
        });
        
        assertEquals("Goal not found", exception.getMessage());
        verify(goalRepository, never()).delete(any(Goal.class));
    }
}
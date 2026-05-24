package com.finance.manager.service;

import com.finance.manager.dto.GoalRequest;
import com.finance.manager.dto.GoalUpdateRequest;
import com.finance.manager.entity.Goal;
import com.finance.manager.entity.Transaction;
import com.finance.manager.entity.User;
import com.finance.manager.repository.GoalRepository;
import com.finance.manager.repository.TransactionRepository;
import com.finance.manager.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GoalService {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public GoalService(GoalRepository goalRepository, UserRepository userRepository, TransactionRepository transactionRepository) {
        this.goalRepository = goalRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    public Goal createGoal(GoalRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Default start date to today if not provided
        LocalDate startDate = request.getStartDate() != null ? request.getStartDate() : LocalDate.now();

        Goal goal = new Goal(
                request.getGoalName(),
                request.getTargetAmount(),
                request.getTargetDate(),
                startDate,
                user
        );

        return goalRepository.save(goal);
    }

    public List<Goal> getAllGoals(Long userId) {
        return goalRepository.findByUserId(userId);
    }

    public Goal getGoalById(Long goalId, Long userId) {
        return goalRepository.findByIdAndUserId(goalId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Goal not found"));
    }

    public Goal updateGoal(Long goalId, GoalUpdateRequest request, Long userId) {
        Goal goal = getGoalById(goalId, userId);

        if (request.getTargetAmount() != null) {
            goal.setTargetAmount(request.getTargetAmount());
        }
        if (request.getTargetDate() != null) {
            goal.setTargetDate(request.getTargetDate());
        }
        
        return goalRepository.save(goal);
    }

    public void deleteGoal(Long goalId, Long userId) {
        Goal goal = getGoalById(goalId, userId);
        goalRepository.delete(goal);
    }

    // --- Dynamic Calculation Logic ---
    public Map<String, Object> calculateGoalProgress(Goal goal, Long userId) {
        // Fetch all transactions
        List<Transaction> transactions = transactionRepository.findByUserIdOrderByDateDesc(userId);
        
        BigDecimal currentProgress = BigDecimal.ZERO;

        // Calculate: (Total Income - Total Expenses) since goal start date
        for (Transaction t : transactions) {
            if (!t.getDate().isBefore(goal.getStartDate())) {
                if (t.getCategory().getType().equals("INCOME")) {
                    currentProgress = currentProgress.add(t.getAmount());
                } else if (t.getCategory().getType().equals("EXPENSE")) {
                    currentProgress = currentProgress.subtract(t.getAmount());
                }
            }
        }

        // Prevent negative progress
        if (currentProgress.compareTo(BigDecimal.ZERO) < 0) {
            currentProgress = BigDecimal.ZERO;
        }

        // Calculate percentage (Progress / Target * 100)
        BigDecimal percentage = BigDecimal.ZERO;
        if (goal.getTargetAmount().compareTo(BigDecimal.ZERO) > 0) {
            percentage = currentProgress.divide(goal.getTargetAmount(), 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
            
            // Cap at 100%
            if (percentage.compareTo(new BigDecimal("100")) > 0) {
                percentage = new BigDecimal("100.00");
            }
        }

        // Calculate remaining amount
        BigDecimal remainingAmount = goal.getTargetAmount().subtract(currentProgress);
        if (remainingAmount.compareTo(BigDecimal.ZERO) < 0) {
            remainingAmount = BigDecimal.ZERO;
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("currentProgress", currentProgress);
        stats.put("progressPercentage", percentage);
        stats.put("remainingAmount", remainingAmount);

        return stats;
    }
}
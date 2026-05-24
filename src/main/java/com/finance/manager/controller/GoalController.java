package com.finance.manager.controller;

import com.finance.manager.dto.GoalRequest;
import com.finance.manager.dto.GoalUpdateRequest;
import com.finance.manager.entity.Goal;
import com.finance.manager.service.GoalService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller responsible for tracking and managing user savings goals.
 */

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private final GoalService goalService;

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    private Long getUserIdOrThrow(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            throw new SecurityException("Unauthorized access");
        }
        return userId;
    }

    // Helper method to combine the Goal entity with the dynamic calculations
    private Map<String, Object> formatGoalResponse(Goal goal, Long userId) {
        Map<String, Object> stats = goalService.calculateGoalProgress(goal, userId);
        
        Map<String, Object> map = new HashMap<>();
        map.put("id", goal.getId());
        map.put("goalName", goal.getGoalName());
        map.put("targetAmount", goal.getTargetAmount());
        map.put("targetDate", goal.getTargetDate().toString());
        map.put("startDate", goal.getStartDate().toString());
        map.put("currentProgress", stats.get("currentProgress"));
        map.put("progressPercentage", stats.get("progressPercentage"));
        map.put("remainingAmount", stats.get("remainingAmount"));
        
        return map;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createGoal(@Valid @RequestBody GoalRequest request, HttpSession session) {
        Long userId = getUserIdOrThrow(session);
        Goal goal = goalService.createGoal(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(formatGoalResponse(goal, userId));
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllGoals(HttpSession session) {
        Long userId = getUserIdOrThrow(session);
        List<Goal> goals = goalService.getAllGoals(userId);

        List<Map<String, Object>> formattedGoals = goals.stream()
                .map(goal -> formatGoalResponse(goal, userId))
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("goals", formattedGoals);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getGoal(@PathVariable Long id, HttpSession session) {
        Long userId = getUserIdOrThrow(session);
        Goal goal = goalService.getGoalById(id, userId);
        return ResponseEntity.ok(formatGoalResponse(goal, userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateGoal(
            @PathVariable Long id, 
            @Valid @RequestBody GoalUpdateRequest request, 
            HttpSession session) {
        
        Long userId = getUserIdOrThrow(session);
        Goal goal = goalService.updateGoal(id, request, userId);
        return ResponseEntity.ok(formatGoalResponse(goal, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteGoal(@PathVariable Long id, HttpSession session) {
        Long userId = getUserIdOrThrow(session);
        goalService.deleteGoal(id, userId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Goal deleted successfully");
        return ResponseEntity.ok(response);
    }
}
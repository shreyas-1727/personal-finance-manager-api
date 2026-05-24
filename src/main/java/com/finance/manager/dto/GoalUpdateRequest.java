package com.finance.manager.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

public class GoalUpdateRequest {
    @Positive(message = "Target amount must be a positive number")
    private BigDecimal targetAmount;
    
    @Future(message = "Target date must be a future date")
    private LocalDate targetDate;

    public BigDecimal getTargetAmount() { return targetAmount; }
    public void setTargetAmount(BigDecimal targetAmount) { this.targetAmount = targetAmount; }
    
    public LocalDate getTargetDate() { return targetDate; }
    public void setTargetDate(LocalDate targetDate) { this.targetDate = targetDate; }
}
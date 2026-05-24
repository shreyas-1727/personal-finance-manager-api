package com.finance.manager.controller;

import com.finance.manager.service.ReportService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    private Long getUserIdOrThrow(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            throw new SecurityException("Unauthorized access");
        }
        return userId;
    }

    @GetMapping("/monthly/{year}/{month}")
    public ResponseEntity<Map<String, Object>> getMonthlyReport(
            @PathVariable int year,
            @PathVariable int month,
            HttpSession session) {
        
        // NEW: Validate the month input
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Invalid month. Must be between 1 and 12.");
        }

        Long userId = getUserIdOrThrow(session);
        return ResponseEntity.ok(reportService.getMonthlyReport(userId, year, month));
    }

    @GetMapping("/yearly/{year}")
    public ResponseEntity<Map<String, Object>> getYearlyReport(
            @PathVariable int year,
            HttpSession session) {
        Long userId = getUserIdOrThrow(session);
        return ResponseEntity.ok(reportService.getYearlyReport(userId, year));
    }
}
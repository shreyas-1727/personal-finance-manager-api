package com.finance.manager.service;

import com.finance.manager.entity.Transaction;
import com.finance.manager.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service class responsible for aggregating transaction data to calculate net savings and category totals.
 */

@Service
public class ReportService {

    private final TransactionRepository transactionRepository;

    public ReportService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    private Map<String, Object> generateReportData(List<Transaction> transactions, int year, Integer month) {
        Map<String, BigDecimal> totalIncome = new HashMap<>();
        Map<String, BigDecimal> totalExpenses = new HashMap<>();
        BigDecimal totalIncomeSum = BigDecimal.ZERO;
        BigDecimal totalExpenseSum = BigDecimal.ZERO;

        for (Transaction t : transactions) {
            String categoryName = t.getCategory().getName();
            BigDecimal amount = t.getAmount();

            if (t.getCategory().getType().equals("INCOME")) {
                totalIncome.put(categoryName, totalIncome.getOrDefault(categoryName, BigDecimal.ZERO).add(amount));
                totalIncomeSum = totalIncomeSum.add(amount);
            } else if (t.getCategory().getType().equals("EXPENSE")) {
                totalExpenses.put(categoryName, totalExpenses.getOrDefault(categoryName, BigDecimal.ZERO).add(amount));
                totalExpenseSum = totalExpenseSum.add(amount);
            }
        }

        BigDecimal netSavings = totalIncomeSum.subtract(totalExpenseSum);

        Map<String, Object> report = new HashMap<>();
        if (month != null) {
            report.put("month", month);
        }
        report.put("year", year);
        report.put("totalIncome", totalIncome);
        report.put("totalExpenses", totalExpenses);
        report.put("netSavings", netSavings);

        return report;
    }

    public Map<String, Object> getMonthlyReport(Long userId, int year, int month) {
        List<Transaction> transactions = transactionRepository.findByUserIdOrderByDateDesc(userId).stream()
                .filter(t -> t.getDate().getYear() == year && t.getDate().getMonthValue() == month)
                .collect(Collectors.toList());

        return generateReportData(transactions, year, month);
    }

    public Map<String, Object> getYearlyReport(Long userId, int year) {
        List<Transaction> transactions = transactionRepository.findByUserIdOrderByDateDesc(userId).stream()
                .filter(t -> t.getDate().getYear() == year)
                .collect(Collectors.toList());

        return generateReportData(transactions, year, null);
    }
}
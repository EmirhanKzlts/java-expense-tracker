package com.expensetracker.expensetracker.dtos.budget;

import com.expensetracker.expensetracker.utils.Period;
import lombok.Data;
import org.springframework.cglib.core.Local;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BudgetResponse {

    private Long id;

    private BigDecimal amount;

    private BigDecimal spent;

    private BigDecimal remaining;

    private BigDecimal usagePercent;

    private boolean overBudget;

    private Period period;

    private LocalDateTime createdAt;

    private LocalDateTime startDate;

    private Long categoryId;

    private String categoryName;

    private String categoryIcon;

    private String categoryColor;


}

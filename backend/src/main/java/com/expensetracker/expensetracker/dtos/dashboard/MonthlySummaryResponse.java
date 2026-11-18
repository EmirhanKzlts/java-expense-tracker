package com.expensetracker.expensetracker.dtos.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
public class MonthlySummaryResponse {

    private String month;
    private BigDecimal income;
    private BigDecimal expense;

    public MonthlySummaryResponse(
            Integer year,
            Integer month,
            BigDecimal income,
            BigDecimal expense
    ) {
        this.month = String.format("%d-%02d", year, month);
        this.income = income;
        this.expense = expense;
    }
}

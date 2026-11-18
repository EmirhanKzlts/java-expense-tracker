package com.expensetracker.expensetracker.dtos.dashboard;

import com.expensetracker.expensetracker.dtos.budget.BudgetResponse;
import com.expensetracker.expensetracker.dtos.transaction.TransactionResponse;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class DashboardSummaryResponse {

    private BigDecimal income;
    private BigDecimal expense;
    private BigDecimal balance;
    private BigDecimal savingRate;

    private List<MonthlySummaryResponse> monthlySummary;
    private List<CategoryExpenseResponse> categoryExpenses;
    private List<TransactionResponse> recentTransactions;
    private List<BudgetResponse> activeBudgets;
}

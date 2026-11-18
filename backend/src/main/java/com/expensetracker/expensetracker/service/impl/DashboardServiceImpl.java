package com.expensetracker.expensetracker.service.impl;

import com.expensetracker.expensetracker.dtos.budget.BudgetResponse;
import com.expensetracker.expensetracker.dtos.dashboard.CategoryExpenseResponse;
import com.expensetracker.expensetracker.dtos.dashboard.DashboardSummaryResponse;
import com.expensetracker.expensetracker.dtos.dashboard.MonthlySummaryResponse;
import com.expensetracker.expensetracker.dtos.transaction.TransactionResponse;
import com.expensetracker.expensetracker.model.User;
import com.expensetracker.expensetracker.repository.TransactionRepository;
import com.expensetracker.expensetracker.service.BudgetService;
import com.expensetracker.expensetracker.service.DashboardService;
import com.expensetracker.expensetracker.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final SecurityUtils securityUtils;
    private final TransactionRepository transactionRepository;
    private final BudgetService budgetService;


    @Override
    public DashboardSummaryResponse getDashboardSummary() {

        User currentUser = securityUtils.getCurrentUser();

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime startDate = now
                .withDayOfMonth(1)
                .toLocalDate()
                .atStartOfDay();

        LocalDateTime endDate = startDate.plusMonths(1);

        LocalDateTime sixMonthsAgo = startDate.minusMonths(5);

        // CURRENT MONTH
        BigDecimal income = transactionRepository.getCurrentMonthIncome(
                currentUser,
                startDate,
                endDate
        );

        BigDecimal expense = transactionRepository.getCurrentMonthExpense(
                currentUser,
                startDate,
                endDate
        );

        BigDecimal balance = income.subtract(expense);

        BigDecimal savingRate = BigDecimal.ZERO;

        if (income.compareTo(BigDecimal.ZERO) > 0) {
            savingRate = balance
                    .multiply(BigDecimal.valueOf(100))
                    .divide(income, 2, RoundingMode.HALF_UP);
        }

        // LAST 6 MONTHS
        List<MonthlySummaryResponse> monthlySummary =
                transactionRepository.getMonthlySummary(
                        currentUser,
                        sixMonthsAgo,
                        endDate
                );

        // CATEGORY EXPENSES
        List<CategoryExpenseResponse> categoryExpenses =
                transactionRepository.getCurrentMonthCategoryExpenses(
                        currentUser,
                        startDate,
                        endDate
                )
                        .stream()
                        .map(row -> {
                            CategoryExpenseResponse response = new CategoryExpenseResponse();

                            response.setCategoryId((Long) row[0]);
                            response.setCategoryName((String) row[1]);
                            response.setExpenseAmount((BigDecimal) row[2]);

                            return  response;
                        })
                        .toList();

        // LAST 5 TRANSACTIONS
        List<TransactionResponse> recentTransactions =
                transactionRepository
                        .findTop5ByUserOrderByTransactionDateDesc(currentUser)
                        .stream()
                        .map(transaction -> {
                            TransactionResponse response = new TransactionResponse();

                            response.setId(transaction.getId());
                            response.setAmount(transaction.getAmount());
                            response.setType(transaction.getType());
                            response.setDescription(transaction.getDescription());
                            response.setCategoryId(
                                    transaction.getCategory() != null
                                    ? transaction.getCategory().getId()
                                            : null
                            );
                            response.setCategoryName(
                                    transaction.getCategory() != null
                                    ? transaction.getCategory().getCategoryName()
                                            : null
                            );
                            response.setTransactionDate(transaction.getTransactionDate());
                            response.setCreatedAt(transaction.getCreatedAt());

                            return response;
                        })
                        .toList();

        List<BudgetResponse> getActiveBudgets =
                budgetService.getActiveBudgets();

        DashboardSummaryResponse dashboardSummaryResponse = new DashboardSummaryResponse();

        dashboardSummaryResponse.setIncome(income);
        dashboardSummaryResponse.setExpense(expense);
        dashboardSummaryResponse.setBalance(balance);
        dashboardSummaryResponse.setSavingRate(savingRate);

        dashboardSummaryResponse.setMonthlySummary(monthlySummary);

        dashboardSummaryResponse.setCategoryExpenses(categoryExpenses);

        dashboardSummaryResponse.setRecentTransactions(recentTransactions);

        dashboardSummaryResponse.setActiveBudgets(getActiveBudgets);

        return dashboardSummaryResponse;
    }
}

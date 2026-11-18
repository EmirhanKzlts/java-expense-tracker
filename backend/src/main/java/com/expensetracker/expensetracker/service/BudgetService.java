package com.expensetracker.expensetracker.service;

import com.expensetracker.expensetracker.dtos.budget.BudgetResponse;
import com.expensetracker.expensetracker.dtos.budget.CreateBudgetRequest;
import java.util.List;

public interface BudgetService {

    BudgetResponse createBudget(CreateBudgetRequest createBudgetRequest);

    List<BudgetResponse> getAllBudgets();

    BudgetResponse updateBudget(Long id, CreateBudgetRequest createBudgetRequest);

    void deleteBudget(Long id);

    List<BudgetResponse> getActiveBudgets();
}

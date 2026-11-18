package com.expensetracker.expensetracker.service.impl;

import com.expensetracker.expensetracker.dtos.budget.BudgetResponse;
import com.expensetracker.expensetracker.dtos.budget.CreateBudgetRequest;
import com.expensetracker.expensetracker.exception.BadRequestException;
import com.expensetracker.expensetracker.model.Budget;
import com.expensetracker.expensetracker.model.Category;
import com.expensetracker.expensetracker.model.User;
import com.expensetracker.expensetracker.repository.BudgetRepository;
import com.expensetracker.expensetracker.repository.CategoryRepository;
import com.expensetracker.expensetracker.repository.TransactionRepository;
import com.expensetracker.expensetracker.service.BudgetService;
import com.expensetracker.expensetracker.utils.SecurityUtils;
import com.expensetracker.expensetracker.utils.Type;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {

    private final SecurityUtils securityUtils;
    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

    private BudgetResponse mapToBudgetResponse(Budget budget) {
        BudgetResponse budgetResponse = new BudgetResponse();

        budgetResponse.setId(budget.getId());
        budgetResponse.setAmount(budget.getAmount());
        budgetResponse.setPeriod(budget.getPeriod());
        budgetResponse.setStartDate(budget.getStartDate());
        budgetResponse.setCreatedAt(budget.getCreatedAt());

        budgetResponse.setCategoryId(budget.getCategory().getId());
        budgetResponse.setCategoryName(budget.getCategory().getCategoryName());
        budgetResponse.setCategoryIcon(budget.getCategory().getIcon());
        budgetResponse.setCategoryColor(budget.getCategory().getColor());

        return budgetResponse;
    }

    @Override
    public BudgetResponse createBudget(CreateBudgetRequest createBudgetRequest) {

        User currentUser = securityUtils.getCurrentUser();

        Category category = categoryRepository
                .findByIdAndUser(createBudgetRequest.getCategoryId(), currentUser)
                .orElseThrow(() -> new RuntimeException("Category Not Found"));

        if (category.getType() != Type.EXPENSE) {
            throw new BadRequestException("Budget can only be created for expense categories");
        }

        if (budgetRepository.existsByUserAndCategory(currentUser, category)) {
            throw new BadRequestException("Budget already exists for this category");
        }

        Budget budget = new Budget();

        budget.setAmount(createBudgetRequest.getAmount());
        budget.setPeriod(createBudgetRequest.getPeriod());
        budget.setStartDate(createBudgetRequest.getStartDate());

        budget.setCategory(category);
        budget.setUser(currentUser);

        budgetRepository.save(budget);

        BudgetResponse budgetResponse = mapToBudgetResponse(budget);

        calculateBudgetStatistics(budget, budgetResponse);

        return budgetResponse;
    }

    @Override
    public List<BudgetResponse> getAllBudgets() {

        User currentUser = securityUtils.getCurrentUser();

        return budgetRepository
                .findByUser(currentUser)
                .stream()
                .map(budget -> {
                    BudgetResponse budgetResponse = mapToBudgetResponse(budget);
                    calculateBudgetStatistics(budget, budgetResponse);
                    return budgetResponse;
                })
                .toList();
    }

    @Override
    public BudgetResponse updateBudget(Long id, CreateBudgetRequest createBudgetRequest) {

        User currentUser = securityUtils.getCurrentUser();

        Budget budget = budgetRepository
                .findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new RuntimeException("Budget not found"));

        Category category = categoryRepository
                .findByIdAndUser(createBudgetRequest.getCategoryId(), currentUser)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (category.getType() == Type.INCOME) {
            throw new BadRequestException("Budget can only be updated for expense categories");
        }

        Optional<Budget> existingBudget = budgetRepository.findByUserAndCategory(currentUser, category);

        if (existingBudget.isPresent() && !existingBudget.get().getId().equals(budget.getId())) {
            throw new BadRequestException("Budget already exists for this category");
        }

        budget.setAmount(createBudgetRequest.getAmount());
        budget.setPeriod(createBudgetRequest.getPeriod());
        budget.setStartDate(createBudgetRequest.getStartDate());
        budget.setCategory(category);
        budgetRepository.save(budget);

        BudgetResponse budgetResponse = mapToBudgetResponse(budget);

        calculateBudgetStatistics(budget, budgetResponse);

        return budgetResponse;
    }

    @Override
    public void deleteBudget(Long id) {

        User currentUser = securityUtils.getCurrentUser();

        Budget budget = budgetRepository
                .findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new RuntimeException("Budget not found"));

        budgetRepository.delete(budget);
    }

    private LocalDateTime getEndDate(Budget budget) {
        return switch (budget.getPeriod()) {
            case WEEKLY ->  budget.getStartDate().plusWeeks(1);
            case MONTHLY ->  budget.getStartDate().plusMonths(1);
            case YEARLY ->  budget.getStartDate().plusYears(1);
        };
    }

    private void calculateBudgetStatistics(Budget budget, BudgetResponse budgetResponse) {

        LocalDateTime endDate = getEndDate(budget);

        BigDecimal spent = transactionRepository.getTotalSpentByCategoryAndPeriod(
                budget.getUser(),
                budget.getCategory(),
                budget.getStartDate(),
                endDate
        );

        BigDecimal remaining = budget.getAmount().subtract(spent);

        boolean overBudget = spent.compareTo(budget.getAmount()) > 0;

        BigDecimal usagePercent = BigDecimal.ZERO;

        if (budget.getAmount().compareTo(BigDecimal.ZERO) > 0) {
            usagePercent = spent
                    .multiply(BigDecimal.valueOf(100))
                    .divide(budget.getAmount(), 0, RoundingMode.HALF_UP);
        }

        budgetResponse.setSpent(spent);
        budgetResponse.setRemaining(remaining);
        budgetResponse.setOverBudget(overBudget);
        budgetResponse.setUsagePercent(usagePercent);
    }

    private boolean isActive(Budget budget) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endDate = getEndDate(budget);

        return !now.isBefore(budget.getStartDate()) && now.isBefore(endDate);
    }

    @Override
    public List<BudgetResponse> getActiveBudgets() {

        User currentUser = securityUtils.getCurrentUser();

        return budgetRepository.findByUser(currentUser)
                .stream()
                .filter(this::isActive)
                .map(budget -> {
                    BudgetResponse budgetResponse = mapToBudgetResponse(budget);
                    calculateBudgetStatistics(budget, budgetResponse);
                    return budgetResponse;
                })
                .toList();
    }
}

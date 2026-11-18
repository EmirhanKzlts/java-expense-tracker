package com.expensetracker.expensetracker.controller;

import com.expensetracker.expensetracker.dtos.budget.BudgetResponse;
import com.expensetracker.expensetracker.dtos.budget.CreateBudgetRequest;
import com.expensetracker.expensetracker.service.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    @PostMapping
    public ResponseEntity<BudgetResponse> createBudget(
            @Valid @RequestBody CreateBudgetRequest createBudgetRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(budgetService.createBudget(createBudgetRequest));
    }

    @GetMapping
    public ResponseEntity<List<BudgetResponse>> getAllBudgets(){
        return ResponseEntity.ok(budgetService.getAllBudgets());
    }

    @PutMapping("/{id}")
    public ResponseEntity<BudgetResponse> updateBudget(
            @PathVariable Long id,
            @Valid @RequestBody CreateBudgetRequest createBudgetRequest) {
        return ResponseEntity.ok(budgetService.updateBudget(id,createBudgetRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BudgetResponse> deleteBudget(
            @PathVariable Long id) {
        budgetService.deleteBudget(id);
        return ResponseEntity.noContent().build();
    }

}

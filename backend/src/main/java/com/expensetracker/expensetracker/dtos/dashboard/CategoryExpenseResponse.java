package com.expensetracker.expensetracker.dtos.dashboard;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CategoryExpenseResponse {

    private Long  categoryId;
    private String categoryName;
    private BigDecimal expenseAmount;
}

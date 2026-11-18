package com.expensetracker.expensetracker.dtos.transaction;

import com.expensetracker.expensetracker.utils.Type;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionResponse {

    private Long id;

    private BigDecimal amount;

    private Type type;

    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime transactionDate;

    private Long categoryId;

    private String categoryName;
}

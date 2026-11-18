package com.expensetracker.expensetracker.dtos.transaction;

import com.expensetracker.expensetracker.utils.Type;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CreateTransactionRequest {

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;

    @NotNull
    private Type type;

    private String description;

    @NotNull
    private Long categoryId;

    @NotNull
    private LocalDateTime transactionDate;
}

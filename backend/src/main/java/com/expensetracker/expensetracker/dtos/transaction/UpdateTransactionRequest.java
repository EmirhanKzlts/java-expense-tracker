package com.expensetracker.expensetracker.dtos.transaction;

import com.expensetracker.expensetracker.utils.Type;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateTransactionRequest {

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;

    @NotNull
    private Type type;

    private String description;

    @NotNull
    private Long categoryId;
}

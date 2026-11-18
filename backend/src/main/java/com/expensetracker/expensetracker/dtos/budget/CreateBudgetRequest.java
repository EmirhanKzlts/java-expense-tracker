package com.expensetracker.expensetracker.dtos.budget;

import com.expensetracker.expensetracker.utils.Period;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CreateBudgetRequest {

    @NotNull
    private Period period;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;

    @NotNull
    private LocalDateTime startDate;

    @NotNull
    private Long categoryId;
}

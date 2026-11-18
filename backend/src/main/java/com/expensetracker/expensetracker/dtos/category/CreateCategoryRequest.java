package com.expensetracker.expensetracker.dtos.category;

import com.expensetracker.expensetracker.utils.Type;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateCategoryRequest {

    @NotBlank
    private String categoryName;

    @NotNull
    private Type type;

    private String icon;

    private String color;

}

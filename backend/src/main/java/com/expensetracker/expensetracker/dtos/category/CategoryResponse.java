package com.expensetracker.expensetracker.dtos.category;

import com.expensetracker.expensetracker.utils.Type;
import lombok.Data;

@Data
public class CategoryResponse {

    private Long id;

    private String categoryName;

    private Type type;

    private String icon;

    private String color;

    private boolean isDefault;
}

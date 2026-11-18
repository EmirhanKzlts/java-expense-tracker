package com.expensetracker.expensetracker.utils;

public record DefaultCategory(
        String name,
        Type type,
        String icon,
        String color
) {
}

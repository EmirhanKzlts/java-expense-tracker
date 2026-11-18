package com.expensetracker.expensetracker.utils;

import java.util.List;

public final class DefaultCategories {

    private DefaultCategories() {
    }

    public static final List<DefaultCategory> CATEGORIES = List.of(
            // Expense Categories
            new DefaultCategory("Food & Dining", Type.EXPENSE, "utensils", "#FF9800"),
            new DefaultCategory("Groceries", Type.EXPENSE, "apple", "#4CAF50"),
            new DefaultCategory("Transportation", Type.EXPENSE, "car", "#2196F3"),
            new DefaultCategory("Rent", Type.EXPENSE, "house", "#795548"),
            new DefaultCategory("Utilities", Type.EXPENSE, "toolbox", "#FFC107"),
            new DefaultCategory("Entertainment", Type.EXPENSE, "film", "#E91E63"),
            new DefaultCategory("Shopping", Type.EXPENSE, "shopping-cart", "#9C27B0"),
            new DefaultCategory("Healthcare", Type.EXPENSE, "hospital", "#F44336"),
            new DefaultCategory("Education", Type.EXPENSE, "school", "#3F51B5"),
            new DefaultCategory("Travel", Type.EXPENSE, "plane", "#00BCD4"),
            new DefaultCategory("Personal Care", Type.EXPENSE, "soap-dispenser-droplet", "#8BC34A"),
            new DefaultCategory("Other Expense", Type.EXPENSE, "receipt-text", "#9E9E9E"),

            // Income Categories
            new DefaultCategory("Salary", Type.INCOME, "wallet", "#4CAF50"),
            new DefaultCategory("Freelance", Type.INCOME, "wallet-cards", "#009688"),
            new DefaultCategory("Investment", Type.INCOME, "trending-up", "#8BC34A"),
            new DefaultCategory("Gift", Type.INCOME, "gift", "#FF4081"),
            new DefaultCategory("Other Income", Type.INCOME, "hand-coins", "#607D8B")
    );
}

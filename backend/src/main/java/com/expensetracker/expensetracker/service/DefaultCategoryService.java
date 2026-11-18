package com.expensetracker.expensetracker.service;

import com.expensetracker.expensetracker.model.Category;
import com.expensetracker.expensetracker.model.User;
import com.expensetracker.expensetracker.repository.CategoryRepository;
import com.expensetracker.expensetracker.utils.DefaultCategories;
import com.expensetracker.expensetracker.utils.DefaultCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DefaultCategoryService {

    private final CategoryRepository categoryRepository;

    public void createDefaultCategories(User user) {

        List<Category> categories = new ArrayList<>();

        for (DefaultCategory defaultCategory : DefaultCategories.CATEGORIES){

            Category category = new Category();

            category.setCategoryName(defaultCategory.name());
            category.setType(defaultCategory.type());
            category.setIcon(defaultCategory.icon());
            category.setColor(defaultCategory.color());
            category.setDefault(true);
            category.setUser(user);

            categories.add(category);
        }
        categoryRepository.saveAll(categories);
    }

}

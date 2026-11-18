package com.expensetracker.expensetracker.service;

import com.expensetracker.expensetracker.dtos.category.CategoryResponse;
import com.expensetracker.expensetracker.dtos.category.CreateCategoryRequest;
import java.util.List;

public interface CategoryService {

    CategoryResponse createCategory(CreateCategoryRequest createCategoryRequest);

    List<CategoryResponse> getAllCategories();

    CategoryResponse updateCategory(Long id, CreateCategoryRequest createCategoryRequest);

    void deleteCategory(Long id);
}

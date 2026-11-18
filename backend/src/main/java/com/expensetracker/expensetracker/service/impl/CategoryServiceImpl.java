package com.expensetracker.expensetracker.service.impl;

import com.expensetracker.expensetracker.dtos.category.CategoryResponse;
import com.expensetracker.expensetracker.dtos.category.CreateCategoryRequest;
import com.expensetracker.expensetracker.exception.BadRequestException;
import com.expensetracker.expensetracker.exception.ResourceNotFoundException;
import com.expensetracker.expensetracker.model.Category;
import com.expensetracker.expensetracker.model.User;
import com.expensetracker.expensetracker.repository.BudgetRepository;
import com.expensetracker.expensetracker.repository.CategoryRepository;
import com.expensetracker.expensetracker.repository.TransactionRepository;
import com.expensetracker.expensetracker.service.CategoryService;
import com.expensetracker.expensetracker.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final SecurityUtils securityUtils;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;

    private CategoryResponse mapToCategoryResponse(Category category) {

        CategoryResponse categoryResponse = new CategoryResponse();

        categoryResponse.setId(category.getId());
        categoryResponse.setCategoryName(category.getCategoryName());
        categoryResponse.setType(category.getType());
        categoryResponse.setIcon(category.getIcon());
        categoryResponse.setColor(category.getColor());
        categoryResponse.setDefault(category.isDefault());

        return categoryResponse;
    }

    @Override
    public CategoryResponse createCategory(CreateCategoryRequest createCategoryRequest) {

        User currentUser = securityUtils.getCurrentUser();

        if (categoryRepository.existsByCategoryNameAndUser(createCategoryRequest.getCategoryName(), currentUser)) {
            throw new BadRequestException("Category already exists");
        }

        Category category = new Category();
        category.setCategoryName(createCategoryRequest.getCategoryName());
        category.setType(createCategoryRequest.getType());
        category.setIcon(createCategoryRequest.getIcon());
        category.setColor(createCategoryRequest.getColor());
        category.setDefault(false);
        category.setUser(currentUser);
        categoryRepository.save(category);

        return mapToCategoryResponse(category);
    }

    @Override
    public List<CategoryResponse> getAllCategories() {

        User currentUser = securityUtils.getCurrentUser();

        return categoryRepository.findByUser(currentUser)
                .stream()
                .map(this::mapToCategoryResponse)
                .toList();
    }

    @Override
    public CategoryResponse updateCategory(Long id, CreateCategoryRequest createCategoryRequest) {

        User currentUser = securityUtils.getCurrentUser();

        Category category = categoryRepository
                .findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        category.setCategoryName(createCategoryRequest.getCategoryName());
        category.setType(createCategoryRequest.getType());
        category.setIcon(createCategoryRequest.getIcon());
        category.setColor(createCategoryRequest.getColor());
        categoryRepository.save(category);

        return mapToCategoryResponse(category);
    }

    @Override
    public void deleteCategory(Long id) {

        User currentUser = securityUtils.getCurrentUser();

        Category category = categoryRepository
                .findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (transactionRepository.existsByCategory(category)) {
            throw new BadRequestException("Cannot delete category because it contains transaction");
        }

        if (budgetRepository.existsByCategory(category)) {
            throw new BadRequestException("Cannot delete category because it contains budget");
        }

        categoryRepository.delete(category);
    }
}

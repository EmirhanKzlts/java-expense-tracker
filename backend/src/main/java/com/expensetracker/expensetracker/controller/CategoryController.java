package com.expensetracker.expensetracker.controller;

import com.expensetracker.expensetracker.dtos.category.CategoryResponse;
import com.expensetracker.expensetracker.dtos.category.CreateCategoryRequest;
import com.expensetracker.expensetracker.repository.CategoryRepository;
import com.expensetracker.expensetracker.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    private final CategoryRepository categoryRepository;

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid @RequestBody CreateCategoryRequest createCategoryRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(createCategoryRequest));
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories(){
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CreateCategoryRequest createCategoryRequest){
        return ResponseEntity.ok(categoryService.updateCategory(id,createCategoryRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CategoryResponse> deleteCategory(
            @PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

}

package com.expensetracker.expensetracker.repository;

import com.expensetracker.expensetracker.model.Category;
import com.expensetracker.expensetracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByUser(User user);

    Optional<Category> findByIdAndUser(Long id, User user);

    boolean existsByCategoryNameAndUser(String categoryName, User user);
}

package com.expensetracker.expensetracker.repository;

import com.expensetracker.expensetracker.model.Budget;
import com.expensetracker.expensetracker.model.Category;
import com.expensetracker.expensetracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    List<Budget> findByUser(User user);

    Optional<Budget> findByIdAndUser(Long id, User user);

    Optional<Budget> findByUserAndCategory(User user, Category category);

    boolean existsByCategory(Category category);

    boolean existsByUserAndCategory(User user, Category category);

}

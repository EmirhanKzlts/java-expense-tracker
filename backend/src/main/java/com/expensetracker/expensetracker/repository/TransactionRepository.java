package com.expensetracker.expensetracker.repository;

import com.expensetracker.expensetracker.dtos.dashboard.MonthlySummaryResponse;
import com.expensetracker.expensetracker.model.Category;
import com.expensetracker.expensetracker.model.Transaction;
import com.expensetracker.expensetracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUserOrderByTransactionDateDesc(User user);

    Optional<Transaction> findByIdAndUser(Long id, User user);

    boolean existsByCategory(Category category);

    Long countByUser(User user);

    Long countByUserAndCategory(User user, Category category);

    List<Transaction> findTop5ByUserOrderByTransactionDateDesc(User user);

    @Query("""
            SELECT COALESCE(SUM(t.amount), 0)
            FROM Transaction t
            WHERE t.user = :user
                AND t.category = :category
                AND t.transactionDate >= :startDate
                AND t.transactionDate < :endDate
        """)

    BigDecimal getTotalSpentByCategoryAndPeriod(
            @Param("user") User user,
            @Param("category") Category category,
            @Param("startDate")LocalDateTime startDate,
            @Param("endDate")LocalDateTime endDate
    );

    // QUERIES FOR DASHBOARD
        // Dashboard Summary Response
    @Query("""
            SELECT COALESCE(SUM(t.amount), 0)
            FROM Transaction t
            WHERE t.user = :user
                AND t.type = com.expensetracker.expensetracker.utils.Type.INCOME
                AND t.transactionDate >= :startDate
                AND t.transactionDate < :endDate
    """)
    BigDecimal getCurrentMonthIncome(
            @Param("user") User user,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("""
            SELECT COALESCE(SUM(t.amount), 0)
            FROM Transaction t
            WHERE t.user = :user
                AND t.type = com.expensetracker.expensetracker.utils.Type.EXPENSE
                AND t.transactionDate >= :startDate
                AND t.transactionDate < :endDate
    """)
    BigDecimal getCurrentMonthExpense(
            @Param("user") User user,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

        // Monthly Summary Response
    @Query("""
        SELECT new com.expensetracker.expensetracker.dtos.dashboard.MonthlySummaryResponse(
                YEAR(t.transactionDate),
                MONTH(t.transactionDate),
                COALESCE(SUM(CASE WHEN t.type = com.expensetracker.expensetracker.utils.Type.INCOME THEN t.amount ELSE 0 END), 0),
                COALESCE(SUM(CASE WHEN t.type = com.expensetracker.expensetracker.utils.Type.EXPENSE THEN t.amount ELSE 0 END), 0)
            )
            FROM Transaction t
            WHERE t.user = :user
            AND t.transactionDate >= :startDate
            AND t.transactionDate < :endDate
            GROUP BY YEAR(t.transactionDate), MONTH(t.transactionDate)
            ORDER BY YEAR(t.transactionDate), MONTH(t.transactionDate)
    """)
    List<MonthlySummaryResponse> getMonthlySummary(
            @Param("user") User user,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

        // CategoryExpenseResponse
    @Query("""
        SELECT
             c.id,
             c.categoryName,
             COALESCE(SUM(t.amount), 0)
        FROM Transaction t
        JOIN t.category c
        WHERE t.user = :user
        AND t.type = com.expensetracker.expensetracker.utils.Type.EXPENSE
        AND t.transactionDate >= :startDate
        AND t.transactionDate < :endDate
        GROUP BY c.id, c.categoryName
        ORDER BY SUM(t.amount) DESC
     """)
    List<Object[]> getCurrentMonthCategoryExpenses(
            @Param("user") User user,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );





}

package com.expensetracker.expensetracker.service.impl;

import com.expensetracker.expensetracker.dtos.transaction.CreateTransactionRequest;
import com.expensetracker.expensetracker.dtos.transaction.TransactionResponse;
import com.expensetracker.expensetracker.dtos.transaction.UpdateTransactionRequest;
import com.expensetracker.expensetracker.model.Category;
import com.expensetracker.expensetracker.model.Transaction;
import com.expensetracker.expensetracker.model.User;
import com.expensetracker.expensetracker.repository.CategoryRepository;
import com.expensetracker.expensetracker.repository.TransactionRepository;
import com.expensetracker.expensetracker.service.TransactionService;
import com.expensetracker.expensetracker.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final SecurityUtils securityUtils;
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;

    public TransactionResponse mapToTransactionResponse(Transaction transaction) {

        TransactionResponse transactionResponse = new TransactionResponse();

        transactionResponse.setId(transaction.getId());
        transactionResponse.setAmount(transaction.getAmount());
        transactionResponse.setType(transaction.getType());
        transactionResponse.setDescription(transaction.getDescription());

        transactionResponse.setCategoryId(transaction.getCategory().getId());
        transactionResponse.setCategoryName(transaction.getCategory().getCategoryName());
        transactionResponse.setTransactionDate(transaction.getTransactionDate());
        transactionResponse.setCreatedAt(transaction.getCreatedAt());

        return transactionResponse;
    }

    @Override
    public TransactionResponse createTransaction(CreateTransactionRequest createTransactionRequest) {

        User currentUser = securityUtils.getCurrentUser();

        Category category = categoryRepository
                .findByIdAndUser(createTransactionRequest.getCategoryId(), currentUser)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (category.getType() != createTransactionRequest.getType()) {
            throw new RuntimeException("Transaction type not match category type");
        }

        Transaction transaction = new Transaction();

        transaction.setAmount(createTransactionRequest.getAmount());
        transaction.setType(createTransactionRequest.getType());
        transaction.setDescription(createTransactionRequest.getDescription());
        transaction.setTransactionDate(createTransactionRequest.getTransactionDate());

        transaction.setCategory(category);
        transaction.setUser(currentUser);

        transactionRepository.save(transaction);

        return mapToTransactionResponse(transaction);
    }

    @Override
    public List<TransactionResponse> getTransactions() {

        User currentUser = securityUtils.getCurrentUser();

        return transactionRepository
                .findByUserOrderByTransactionDateDesc(currentUser)
                .stream()
                .map(this::mapToTransactionResponse)
                .toList();
    }

    @Override
    public TransactionResponse getTransaction(Long id) {

        User currentUser = securityUtils.getCurrentUser();

        Transaction transaction = transactionRepository
                .findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        return mapToTransactionResponse(transaction);
    }

    @Override
    public TransactionResponse updateTransaction(Long id, UpdateTransactionRequest updateTransactionRequest) {

        User currentUser = securityUtils.getCurrentUser();

        Transaction transaction = transactionRepository
                .findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        Category category = categoryRepository
                .findByIdAndUser(updateTransactionRequest.getCategoryId(), currentUser)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (category.getType() != updateTransactionRequest.getType()) {
            throw new RuntimeException("Transaction type not match category type");
        }

        transaction.setAmount(updateTransactionRequest.getAmount());
        transaction.setDescription(updateTransactionRequest.getDescription());
        transaction.setCategory(category);
        transactionRepository.save(transaction);

        return  mapToTransactionResponse(transaction);
    }

    @Override
    public void deleteTransaction(Long id) {

        User currentUser = securityUtils.getCurrentUser();

        Transaction transaction = transactionRepository
                .findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        transactionRepository.delete(transaction);
    }
}

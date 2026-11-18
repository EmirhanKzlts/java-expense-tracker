package com.expensetracker.expensetracker.service;

import com.expensetracker.expensetracker.dtos.transaction.CreateTransactionRequest;
import com.expensetracker.expensetracker.dtos.transaction.TransactionResponse;
import com.expensetracker.expensetracker.dtos.transaction.UpdateTransactionRequest;
import java.util.List;

public interface TransactionService {

    TransactionResponse createTransaction(CreateTransactionRequest createTransactionRequest);

    List<TransactionResponse> getTransactions();

    TransactionResponse getTransaction(Long id);

    TransactionResponse updateTransaction(Long id, UpdateTransactionRequest updateTransactionRequest);

    void deleteTransaction(Long id);


}

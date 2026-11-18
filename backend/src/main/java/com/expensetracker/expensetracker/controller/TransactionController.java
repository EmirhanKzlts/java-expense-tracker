package com.expensetracker.expensetracker.controller;

import com.expensetracker.expensetracker.dtos.transaction.CreateTransactionRequest;
import com.expensetracker.expensetracker.dtos.transaction.TransactionResponse;
import com.expensetracker.expensetracker.dtos.transaction.UpdateTransactionRequest;
import com.expensetracker.expensetracker.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
            @Valid @RequestBody CreateTransactionRequest createTransactionRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.createTransaction(createTransactionRequest));
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getTransactions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransaction(
            @PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getTransaction(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> updateTransaction(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTransactionRequest updateTransactionRequest){
        return ResponseEntity.ok(transactionService.updateTransaction(id, updateTransactionRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<TransactionResponse> deleteTransaction(
            @PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }
}

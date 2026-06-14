package pk.fg.pasir_gorka_filip.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pk.fg.pasir_gorka_filip.dto.TransactionDTO;
import pk.fg.pasir_gorka_filip.model.Transaction;
import pk.fg.pasir_gorka_filip.service.TransactionService;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    // Podłączenie Serwisu zamiast Repozytorium [cite: 361-365]
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // 1. GET - pobieranie wszystkich [cite: 367-370]
    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    // 2. GET - pobieranie jednej po ID [cite: 371-375]
    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }

    // 3. POST - tworzenie z użyciem bezpiecznego DTO (Praca samodzielna)
    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@Valid @RequestBody TransactionDTO transactionDTO) {
        Transaction savedTransaction = transactionService.createTransaction(transactionDTO);
        return ResponseEntity.ok(savedTransaction);
    }

    // 4. PUT - aktualizacja istniejącej (z użyciem DTO) [cite: 376-384]
    @PutMapping("/{id}")
    public ResponseEntity<Transaction> updateTransaction(
            @PathVariable Long id,
            @Valid @RequestBody TransactionDTO transactionDTO) {
        Transaction updatedTransaction = transactionService.updateTransaction(id, transactionDTO);
        return ResponseEntity.ok(updatedTransaction);
    }

    // 5. DELETE - usuwanie (Praca samodzielna)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }
}
package pk.fg.pasir_gorka_filip.controller;

import pk.fg.pasir_gorka_filip.dto.BalanceDTO;
import pk.fg.pasir_gorka_filip.model.Transaction;
import pk.fg.pasir_gorka_filip.model.User;
import pk.fg.pasir_gorka_filip.service.TransactionService;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import java.util.List;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import jakarta.validation.Valid;
import pk.fg.pasir_gorka_filip.dto.TransactionDTO;

@Controller
public class TransactionGraphQLController {

    private final TransactionService transactionService;

    public TransactionGraphQLController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @QueryMapping
    public List<Transaction> transactions() {
        return transactionService.getAllTransactions();
    }

    @MutationMapping
    public Transaction addTransaction(@Valid @Argument TransactionDTO transactionDTO) {
        return transactionService.createTransaction(transactionDTO);
    }

    @MutationMapping
    public Transaction updateTransaction(@Argument Long id, @Valid @Argument TransactionDTO transactionDTO) {
        return transactionService.updateTransaction(id, transactionDTO);
    }


    @MutationMapping
    public Boolean deleteTransaction(@Argument Long id) {
        transactionService.deleteTransaction(id);
        return true;
    }

    @QueryMapping
    public BalanceDTO userBalance(@Argument Double days) {
        User user = transactionService.getCurrentUser();
        return transactionService.getUserBalance(user, days);
    }

}

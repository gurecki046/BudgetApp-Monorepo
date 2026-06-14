package pk.fg.pasir_gorka_filip.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double amount; // Kwota transakcji

    @Enumerated(EnumType.STRING)
    private TransactionType type; // Typ (INCOME lub EXPENSE)

    private String tags; // Tagi
    private String notes; // Notatki
    private LocalDateTime timestamp = LocalDateTime.now();



    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


    // Konstruktor z parametrami
    public Transaction(Double amount, TransactionType type, String tags, String notes, User user) {
        this.amount = amount;
        this.type = type;
        this.tags = tags;
        this.notes = notes;
        this.timestamp = LocalDateTime.now();
        this.user = user;
    }


}
package pk.fg.pasir_gorka_filip.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pk.fg.pasir_gorka_filip.model.Debt;
import pk.fg.pasir_gorka_filip.model.User;


import java.util.List;

@Repository
public interface DebtRepository extends JpaRepository<Debt, Long> {
    List<Debt> findByGroupId(Long groupId);
    void deleteByGroupId(Long groupId);

    List<Debt> findByDebtorOrCreditor(User debtor, User creditor);
}

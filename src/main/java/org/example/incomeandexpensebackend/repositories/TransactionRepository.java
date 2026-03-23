package org.example.incomeandexpensebackend.repositories;

import org.example.incomeandexpensebackend.entities.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
    Optional<TransactionEntity> findByDebtId(Long debtId);

    void deleteByDebtId(Long debtId);

    List<TransactionEntity> findByUserId(Long id);

    List<TransactionEntity> findByDateBetween(
            LocalDate start,
            LocalDate end
    );

    List<TransactionEntity> findByUserIdAndDateBetween(
            Long userId,
            LocalDate start,
            LocalDate end
    );

}

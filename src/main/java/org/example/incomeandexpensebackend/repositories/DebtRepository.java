package org.example.incomeandexpensebackend.repositories;

import org.example.incomeandexpensebackend.entities.DebtEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DebtRepository extends JpaRepository<DebtEntity, Long> {
    @Query("""
    SELECT DISTINCT d FROM debts d
    JOIN d.transactions t
    WHERE t.user.id = :userId
""")
    List<DebtEntity> findByUserId(@Param("userId") Long userId);

}

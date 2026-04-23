package org.example.incomeandexpensebackend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.incomeandexpensebackend.enums.CategoryEnum;
import org.example.incomeandexpensebackend.enums.TransactionTypeEnum;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Entity(name = "transactions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private UserEntity user;

    private double amount;

    @Enumerated(EnumType.STRING)
    private TransactionTypeEnum type;

    @Enumerated(EnumType.STRING)
    private CategoryEnum category;

    private String description;

    private LocalDate date;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "debt_id")
    private DebtEntity debt;
}
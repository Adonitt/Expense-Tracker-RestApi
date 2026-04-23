package org.example.incomeandexpensebackend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.incomeandexpensebackend.enums.DebtStatus;
import org.example.incomeandexpensebackend.enums.DebtTypeEnum;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@Entity(name = "debts")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DebtEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double amount;

    private double remainingAmount;

    private double paidAmount;

    private String person;

    private String description;

    @Enumerated(EnumType.STRING)
    private DebtTypeEnum type;

    @Enumerated(EnumType.STRING)
    private DebtStatus status;

    private LocalDate date;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "debt", cascade = CascadeType.ALL)
    private List<TransactionEntity> transactions;

    private LocalDateTime lastPaymentAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
}
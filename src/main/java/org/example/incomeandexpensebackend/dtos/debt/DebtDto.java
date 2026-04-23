package org.example.incomeandexpensebackend.dtos.debt;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.incomeandexpensebackend.entities.TransactionEntity;
import org.example.incomeandexpensebackend.enums.DebtStatus;
import org.example.incomeandexpensebackend.enums.DebtTypeEnum;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DebtDto {

    private Long id;

    @NotNull(message = "Amount cannot be null")
    @Min(value = 0, message = "Amount cannot be negative")
    private Double amount;

    @NotNull(message = "Person cannot be null")
    @NotBlank(message = "Person cannot be blank")
    @Size(min = 2, max = 50, message = "Person must be between 2 and 50 characters long")
    private String person;

    @NotNull(message = "Description cannot be null")
    @NotBlank(message = "Description cannot be blank")
    @Size(min = 2, max = 100, message = "Description must be between 2 and 100 characters long")
    private String description;

    // 🔥 CORE FIELDS
    private Double remainingAmount;
    private Double paidAmount;

    private DebtTypeEnum type;

    private DebtStatus status;

    @NotNull(message = "Date cannot be null")
    @PastOrPresent(message = "Date must be in the past or present")
    private LocalDate date;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Boolean completed;
    private LocalDateTime lastPaymentAt;
}
package org.example.incomeandexpensebackend.dtos.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.incomeandexpensebackend.enums.CategoryEnum;
import org.example.incomeandexpensebackend.enums.TransactionTypeEnum;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionListingDto {
    private Long id;

    private Long debtId;

    private Long userId;

    private String userFullName;

    private double amount;

    private TransactionTypeEnum type;

    private LocalDate date;

    private CategoryEnum category;
}

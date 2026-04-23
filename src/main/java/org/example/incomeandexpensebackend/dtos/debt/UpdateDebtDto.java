package org.example.incomeandexpensebackend.dtos.debt;

import lombok.Data;
import org.example.incomeandexpensebackend.enums.DebtStatus;
import org.example.incomeandexpensebackend.enums.DebtTypeEnum;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;

@Data
public class UpdateDebtDto {

    @NotNull
    private Double amount;

    @NotNull
    private String person;

    @NotNull
    private String description;

    private DebtTypeEnum type;

    private DebtStatus status;

    private LocalDate date;
}
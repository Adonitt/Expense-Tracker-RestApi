package org.example.incomeandexpensebackend.dtos.debt;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayDebtDto {
    @NotNull
    @Min(value = 1, message = "Amount must be greater than 0")
    private double amount;

    private LocalDateTime paymentDate;

}

package org.example.incomeandexpensebackend.services.interfaces;

import org.example.incomeandexpensebackend.dtos.debt.DebtDto;
import org.example.incomeandexpensebackend.dtos.debt.PayDebtDto;
import org.example.incomeandexpensebackend.dtos.debt.UpdateDebtDto;
import org.example.incomeandexpensebackend.services.base_services.*;

public interface DebtService extends Addable<DebtDto>, FindAll<DebtDto>, FindById<DebtDto, Long>, Removable<Long> {

    public DebtDto update(Long id, UpdateDebtDto dto);


    DebtDto payDebt(Long id, PayDebtDto dto);
}

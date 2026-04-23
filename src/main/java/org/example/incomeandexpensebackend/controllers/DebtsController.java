package org.example.incomeandexpensebackend.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.incomeandexpensebackend.dtos.debt.DebtDto;
import org.example.incomeandexpensebackend.dtos.debt.PayDebtDto;
import org.example.incomeandexpensebackend.dtos.debt.UpdateDebtDto;
import org.example.incomeandexpensebackend.services.interfaces.DebtService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/debts")
@RequiredArgsConstructor
public class DebtsController {

    private final DebtService debtService;

    @PostMapping
    public DebtDto create(@RequestBody DebtDto dto) {
        return debtService.create(dto);
    }

    @GetMapping
    public List<DebtDto> getAll() {
        return debtService.findAll();
    }

    @GetMapping("/{id}")
    public DebtDto getById(@PathVariable Long id) {
        return debtService.findById(id);
    }

    // 🔥 PAY ENDPOINT
    @PostMapping("/{id}/pay")
    public DebtDto pay(@PathVariable Long id,
                       @RequestBody PayDebtDto dto) {
        return debtService.payDebt(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        debtService.removeById(id);
    }

    @PutMapping("/{id}")
    public DebtDto update(@PathVariable Long id,
                          @RequestBody @Valid UpdateDebtDto dto) {
        return debtService.update(id, dto);
    }
}
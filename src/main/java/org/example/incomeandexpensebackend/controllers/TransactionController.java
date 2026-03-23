package org.example.incomeandexpensebackend.controllers;

import lombok.RequiredArgsConstructor;
import org.example.incomeandexpensebackend.dtos.transaction.CreateTransactionDto;
import org.example.incomeandexpensebackend.dtos.transaction.TransactionDetailsDto;
import org.example.incomeandexpensebackend.dtos.transaction.TransactionListingDto;
import org.example.incomeandexpensebackend.dtos.transaction.UpdateTransactionDto;
import org.example.incomeandexpensebackend.services.interfaces.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transactions")
public class TransactionController {
    private final TransactionService service;

    @GetMapping
    public ResponseEntity<List<TransactionListingDto>> getAllTransactions() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionDetailsDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping("/create")
    public ResponseEntity<CreateTransactionDto> create(@RequestBody CreateTransactionDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<UpdateTransactionDto> editById(@PathVariable Long id, @RequestBody UpdateTransactionDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeById(@PathVariable Long id) {
        service.removeById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/default/create")
    public CreateTransactionDto defaultCreate() {
        return new CreateTransactionDto();
    }

    @GetMapping("/default/update")
    public UpdateTransactionDto defaultUpdate() {
        return new UpdateTransactionDto();
    }

    @GetMapping("/filter")
    public ResponseEntity<List<TransactionListingDto>> filterByDate(
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam(required = false) Integer day
    ) {
        return ResponseEntity.ok(service.findByYearMonthDay(year, month, day));
    }


}

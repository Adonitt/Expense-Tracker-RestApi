package org.example.incomeandexpensebackend.services.implementations;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.incomeandexpensebackend.dtos.debt.DebtDto;
import org.example.incomeandexpensebackend.dtos.debt.PayDebtDto;
import org.example.incomeandexpensebackend.dtos.debt.UpdateDebtDto;
import org.example.incomeandexpensebackend.entities.DebtEntity;
import org.example.incomeandexpensebackend.entities.TransactionEntity;
import org.example.incomeandexpensebackend.entities.UserEntity;
import org.example.incomeandexpensebackend.enums.*;
import org.example.incomeandexpensebackend.mappers.DebtMapper;
import org.example.incomeandexpensebackend.repositories.DebtRepository;
import org.example.incomeandexpensebackend.repositories.TransactionRepository;
import org.example.incomeandexpensebackend.repositories.UserRepository;
import org.example.incomeandexpensebackend.services.interfaces.AuthService;
import org.example.incomeandexpensebackend.services.interfaces.DebtService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DebtServiceImpl implements DebtService {

    private final DebtRepository debtRepository;
    private final DebtMapper debtMapper;
    private final TransactionRepository transactionRepository;
    private final AuthService authService;
    private final UserRepository userRepository;

    // 🔥 GET LOGGED USER
    private UserEntity getLoggedUser() {
        String email = authService.getLoggedInUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // 🔥 SECURITY CHECK
    private void checkOwnership(DebtEntity debt, UserEntity user) {
        if (user.getRole() != RoleEnum.ADMIN &&
                !debt.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access");
        }
    }

    @Override
    public DebtDto create(DebtDto dto) {

        UserEntity user = getLoggedUser();

        DebtEntity debt = new DebtEntity();
        debt.setUser(user); // 🔥 IMPORTANT
        debt.setAmount(dto.getAmount());
        debt.setRemainingAmount(dto.getAmount());
        debt.setPaidAmount(0);
        debt.setPerson(dto.getPerson());
        debt.setDescription(dto.getDescription());
        debt.setType(dto.getType());
        debt.setStatus(dto.getStatus() != null ? dto.getStatus() : DebtStatus.PENDING);
        debt.setDate(dto.getDate());
        debt.setCreatedAt(LocalDateTime.now());

        DebtEntity savedDebt = debtRepository.save(debt);

        // 🔥 INITIAL TRANSACTION
        TransactionEntity tx = new TransactionEntity();
        tx.setUser(user);
        tx.setAmount(dto.getAmount());
        tx.setDate(dto.getDate());
        tx.setDescription("Initial debt");
        tx.setCategory(CategoryEnum.DEBT);
        tx.setDebt(savedDebt);

        tx.setType(debt.getType() == DebtTypeEnum.LENT
                ? TransactionTypeEnum.EXPENSE
                : TransactionTypeEnum.INCOME);

        transactionRepository.save(tx);

        return debtMapper.toDto(savedDebt);
    }

    @Override
    public List<DebtDto> findAll() {

        UserEntity user = getLoggedUser();

        List<DebtEntity> debts;

        if (user.getRole() == RoleEnum.ADMIN) {
            debts = debtRepository.findAll();
        } else {
            debts = debtRepository.findByUserId(user.getId());
        }

        return debtMapper.toDtoList(debts);
    }

    @Override
    public DebtDto findById(Long id) {

        UserEntity user = getLoggedUser();

        DebtEntity debt = debtRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Debt not found"));

        checkOwnership(debt, user);

        return debtMapper.toDto(debt);
    }

    @Override
    @Transactional
    public DebtDto payDebt(Long id, PayDebtDto dto) {

        UserEntity user = getLoggedUser();

        DebtEntity debt = debtRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Debt not found"));

        checkOwnership(debt, user);

        double payAmount = dto.getAmount();

        if (payAmount <= 0)
            throw new RuntimeException("Invalid amount");

        if (payAmount > debt.getRemainingAmount())
            throw new RuntimeException("Too much payment");

        // 🔥 UPDATE DEBT
        debt.setRemainingAmount(debt.getRemainingAmount() - payAmount);
        debt.setPaidAmount(debt.getPaidAmount() + payAmount);
        debt.setLastPaymentAt(LocalDateTime.now());

        debt.setStatus(debt.getRemainingAmount() == 0
                ? DebtStatus.PAID
                : DebtStatus.PENDING);

        // 🔥 TRANSACTION
        TransactionEntity tx = new TransactionEntity();
        tx.setUser(user);
        tx.setAmount(payAmount);
        tx.setDate(LocalDate.now());
        tx.setCategory(CategoryEnum.DEBT);
        tx.setDescription("Debt payment");
        tx.setDebt(debt);

        tx.setType(debt.getType() == DebtTypeEnum.LENT
                ? TransactionTypeEnum.INCOME
                : TransactionTypeEnum.EXPENSE);

        transactionRepository.save(tx);

        debtRepository.save(debt);

        return debtMapper.toDto(debt);
    }

    @Override
    public DebtDto update(Long id, UpdateDebtDto dto) {

        UserEntity user = getLoggedUser();

        DebtEntity debt = debtRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Debt not found"));

        checkOwnership(debt, user);

        if (dto.getAmount() < debt.getAmount()) {
            throw new RuntimeException("New Amount must be greater than or equal to the current amount");
        }

        debt.setAmount(dto.getAmount());
        debt.setPerson(dto.getPerson());
        debt.setDescription(dto.getDescription());
        debt.setType(dto.getType());
        debt.setDate(dto.getDate());
        debt.setUpdatedAt(LocalDateTime.now());

        double paid = debt.getPaidAmount();
        double remaining = dto.getAmount() - paid;

        debt.setRemainingAmount(Math.max(remaining, 0));


        return debtMapper.toDto(debtRepository.save(debt));
    }

    @Override
    public void removeById(Long id) {

        UserEntity user = getLoggedUser();

        DebtEntity debt = debtRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Debt not found"));

        checkOwnership(debt, user);

        debtRepository.deleteById(id);
    }
}
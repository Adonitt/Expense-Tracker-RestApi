package org.example.incomeandexpensebackend.services.implementations;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.incomeandexpensebackend.dtos.transaction.CreateTransactionDto;
import org.example.incomeandexpensebackend.dtos.transaction.TransactionDetailsDto;
import org.example.incomeandexpensebackend.dtos.transaction.TransactionListingDto;
import org.example.incomeandexpensebackend.dtos.transaction.UpdateTransactionDto;
import org.example.incomeandexpensebackend.entities.TransactionEntity;
import org.example.incomeandexpensebackend.entities.UserEntity;
import org.example.incomeandexpensebackend.enums.CategoryEnum;
import org.example.incomeandexpensebackend.enums.RoleEnum;
import org.example.incomeandexpensebackend.exceptions.DateAllowanceException;
import org.example.incomeandexpensebackend.exceptions.DebtTransactionException;
import org.example.incomeandexpensebackend.exceptions.UnauthorizedException;
import org.example.incomeandexpensebackend.mappers.TransactionMapper;
import org.example.incomeandexpensebackend.repositories.TransactionRepository;
import org.example.incomeandexpensebackend.repositories.UserRepository;
import org.example.incomeandexpensebackend.services.interfaces.AuthService;
import org.example.incomeandexpensebackend.services.interfaces.TransactionService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final TransactionMapper mapper;
    private final AuthService authService;

    @Override
    public CreateTransactionDto create(CreateTransactionDto dto) {
        String email = authService.getLoggedInUserEmail();
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Përdoruesi nuk u gjet"));

        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new UnauthorizedException(
                    "Nuk ju lejohet të shtoni një transaksion sepse llogaria juaj nuk është aktive! Ju lutemi kontaktoni mbështetjen."
            );
        }

        var entity = mapper.toEntity(dto);

        if (entity.getDate().isAfter(LocalDate.now())) {
            throw new DateAllowanceException(
                    "Nuk lejohet të shtoni transaksione për muajt e ardhshëm!"
            );
        }

        entity.setUser(user);
        entity.setCreatedAt(LocalDateTime.now());
        user.addTransaction(entity);

        var saved = transactionRepository.save(entity);
        return mapper.toCreateDto(saved);
    }

    @Override
    public List<TransactionListingDto> findAll() {

        String email = authService.getLoggedInUserEmail();
        UserEntity loggedInUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Përdoruesi nuk u gjet"));

        List<TransactionEntity> transactionsList;

        if (loggedInUser.getRole().equals(RoleEnum.ADMIN)) {
            transactionsList = transactionRepository.findAll();
        } else {
            transactionsList = transactionRepository.findByUserId(loggedInUser.getId());
        }

        return mapper.toTransactionListingDtoList(transactionsList);
    }

    @Override
    public TransactionDetailsDto findById(Long id) {
        var transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaksioni nuk u gjet"));

        String email = authService.getLoggedInUserEmail();
        UserEntity loggedInUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Përdoruesi nuk u gjet"));

        if (loggedInUser.getRole() != RoleEnum.ADMIN &&
                !transaction.getUser().getId().equals(loggedInUser.getId())) {
            throw new UnauthorizedException("Nuk ju lejohet të aksesoni këtë transaksion");
        }

        return mapper.toTransactionDetailsDto(transaction);
    }

    @Override
    public UpdateTransactionDto update(Long id, UpdateTransactionDto dto) {

        TransactionEntity entity = transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaksioni nuk u gjet"));


        if (!Boolean.TRUE.equals(entity.getUser().getIsActive())) {
            throw new UnauthorizedException(
                    "Nuk ju lejohet të përditësoni një transaksion sepse llogaria juaj nuk është aktive! Ju lutemi kontaktoni mbështetjen."
            );
        }

        if (dto.getDate().isAfter(LocalDate.now())) {
            throw new DateAllowanceException(
                    "Nuk lejohet të shtoni transaksione për muajt e ardhshëm!"
            );
        }

        entity.setAmount(dto.getAmount());
        entity.setType(dto.getType());
        entity.setCategory(dto.getCategory());
        entity.setDescription(dto.getDescription());
        entity.setDate(dto.getDate());
        entity.setUpdatedAt(LocalDateTime.now());

        var savedEntity = transactionRepository.save(entity);
        return mapper.toUpdateDto(savedEntity);
    }

    @Override
    public void removeById(Long id) {
        var transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaksioni nuk u gjet"));

        if (!Boolean.TRUE.equals(transaction.getUser().getIsActive())) {
            throw new UnauthorizedException(
                    "Nuk ju lejohet të fshini një transaksion sepse llogaria juaj nuk është aktive! Ju lutemi kontaktoni mbështetjen."
            );
        }



        transactionRepository.deleteById(id);
    }

    @Override
    public List<TransactionListingDto> findByDateRange(LocalDate fromDate, LocalDate toDate) {

        String email = authService.getLoggedInUserEmail();
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Përdoruesi nuk u gjet"));

        List<TransactionEntity> transactions;

        if (user.getRole() == RoleEnum.ADMIN) {
            transactions = transactionRepository.findByDateBetween(fromDate, toDate);
        } else {
            transactions = transactionRepository.findByUserIdAndDateBetween(
                    user.getId(), fromDate, toDate
            );
        }

        return mapper.toTransactionListingDtoList(transactions);
    }
}
package org.example.incomeandexpensebackend.mappers;

import org.example.incomeandexpensebackend.dtos.transaction.CreateTransactionDto;
import org.example.incomeandexpensebackend.dtos.transaction.TransactionDetailsDto;
import org.example.incomeandexpensebackend.dtos.transaction.TransactionListingDto;
import org.example.incomeandexpensebackend.dtos.transaction.UpdateTransactionDto;
import org.example.incomeandexpensebackend.entities.TransactionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    TransactionEntity toEntity(CreateTransactionDto dto);

    CreateTransactionDto toCreateDto(TransactionEntity entity);

    @Mapping(target = "userId", expression = "java(entity.getUser().getId())")
    @Mapping(target = "debtId", expression = "java(entity.getDebt() != null ? entity.getDebt().getId() : null)")
    @Mapping(target = "userFullName", expression = "java(entity.getUser().getFirstName() + \" \" + entity.getUser().getLastName())")
    TransactionListingDto toTransactionListingDto(TransactionEntity entity);

    List<TransactionListingDto> toTransactionListingDtoList(List<TransactionEntity> transactionEntities);

    @Mapping(target = "userId", expression = "java(entity.getUser().getId())")
    @Mapping(target = "debtId", expression = "java(entity.getDebt()!= null ? entity.getDebt().getId() : null)")
    @Mapping(target = "userFullName", expression = "java(entity.getUser().getFirstName() + \" \" + entity.getUser().getLastName())")
    TransactionDetailsDto toTransactionDetailsDto(TransactionEntity entity);

    UpdateTransactionDto toUpdateDto(TransactionEntity entity);
}

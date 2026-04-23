package org.example.incomeandexpensebackend.mappers;

import org.example.incomeandexpensebackend.dtos.debt.DebtDto;
import org.example.incomeandexpensebackend.dtos.debt.UpdateDebtDto;
import org.example.incomeandexpensebackend.entities.DebtEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DebtMapper {

    @Mapping(target = "remainingAmount", source = "remainingAmount")
    @Mapping(target = "paidAmount", source = "paidAmount")
    DebtDto toDto(DebtEntity entity);


    List<DebtDto> toDtoList(List<DebtEntity> entities);
}
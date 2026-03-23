package org.example.incomeandexpensebackend.services.implementations;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.incomeandexpensebackend.dtos.user.CreateUserDto;
import org.example.incomeandexpensebackend.dtos.user.UpdateUserDto;
import org.example.incomeandexpensebackend.dtos.user.UserDetailsDto;
import org.example.incomeandexpensebackend.dtos.user.UserListingDto;
import org.example.incomeandexpensebackend.entities.UserEntity;
import org.example.incomeandexpensebackend.enums.RoleEnum;
import org.example.incomeandexpensebackend.exceptions.EmailExistsException;
import org.example.incomeandexpensebackend.exceptions.PasswordsDoNotMatchException;
import org.example.incomeandexpensebackend.mappers.UserMapper;
import org.example.incomeandexpensebackend.repositories.TransactionRepository;
import org.example.incomeandexpensebackend.repositories.UserRepository;
import org.example.incomeandexpensebackend.services.interfaces.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final TransactionRepository transactionRepository;

    @Override
    public CreateUserDto create(CreateUserDto dto) {

        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new PasswordsDoNotMatchException("Passwords do not match");
        }

        var existing = userRepository.findByEmail(dto.getEmail());
        if (existing.isPresent()) {
            throw new EmailExistsException("Email already exists");
        }


        var entity = userMapper.toEntity(dto);
        entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        entity.setRole(RoleEnum.USER);
        entity.setRegisteredAt(LocalDateTime.now());
        entity.setIsActive(true);

        var saved = userRepository.save(entity);
        return userMapper.toDto(saved);
    }

    @Override
    public List<UserListingDto> findAll() {
        var userList = userRepository.findAll();
        return userMapper.toUserListingDtoList(userList);
    }

    @Override
    public UserDetailsDto findById(Long id) {
        var user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
        return userMapper.toUserDetailsDto(user);
    }

    @Override
    public UpdateUserDto update(Long id, UpdateUserDto dto) {
        var user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());
        user.setIsActive(dto.getIsActive());

        var updatedUser = userRepository.save(user);
        return userMapper.toUpdateUserDto(updatedUser);
    }

    @Transactional
    @Override
    public void removeById(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        transactionRepository.deleteAll(user.getTransactions());

        userRepository.delete(user);
    }
}

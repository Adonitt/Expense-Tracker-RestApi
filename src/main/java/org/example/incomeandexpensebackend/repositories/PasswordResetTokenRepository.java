package org.example.incomeandexpensebackend.repositories;

import org.example.incomeandexpensebackend.entities.PasswordResetTokenEntity;
import org.example.incomeandexpensebackend.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetTokenEntity, Long> {
    Optional<PasswordResetTokenEntity> findByToken(String token);
    void deleteByUser(UserEntity user);

}

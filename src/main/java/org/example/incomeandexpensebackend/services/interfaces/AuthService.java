package org.example.incomeandexpensebackend.services.interfaces;

import org.example.incomeandexpensebackend.dtos.auth.AuthResponseDto;
import org.example.incomeandexpensebackend.dtos.auth.ChangePasswordDto;
import org.example.incomeandexpensebackend.dtos.auth.LoginDto;
import org.example.incomeandexpensebackend.entities.UserEntity;

public interface AuthService {
    public AuthResponseDto login(LoginDto dto);

    UserEntity validateToken(String token);

    String getLoggedInUserEmail();

    void changePassword(ChangePasswordDto req, String userEmail);

    Long getLoggedInUserId();

    String getLoggedInUserRole();
}

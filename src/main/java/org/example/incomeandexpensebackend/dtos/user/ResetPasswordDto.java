package org.example.incomeandexpensebackend.dtos.user;

import lombok.Data;

@Data
public class ResetPasswordDto {

    private String token;
    private String password;
    private String confirmPassword;
}
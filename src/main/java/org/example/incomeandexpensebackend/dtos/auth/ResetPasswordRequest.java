package org.example.incomeandexpensebackend.dtos.auth;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordRequest {
    private String token;
    @Size(min = 6)
    private String newPassword;
    @Size(min = 6)
    private String confirmPassword;
}

package org.example.incomeandexpensebackend.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.incomeandexpensebackend.dtos.auth.*;
import org.example.incomeandexpensebackend.dtos.user.CreateUserDto;
import org.example.incomeandexpensebackend.services.interfaces.AuthService;
import org.example.incomeandexpensebackend.services.interfaces.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginDto loginDto) {
        AuthResponseDto response = authService.login(loginDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<CreateUserDto> register(@RequestBody CreateUserDto createUserDto) {
        return ResponseEntity.ok(userService.create(createUserDto));
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePasswordDtoResponseEntity(@RequestBody @Valid ChangePasswordDto request) {
        String email = authService.getLoggedInUserEmail();
        authService.changePassword(request, email);
        Map<String, String> resp = new HashMap<>();
        resp.put("message", "Passwordi eshte ndryshuar me sukses.");
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequestDto dto) {
        authService.forgotPassword(dto.getEmail());
        System.out.println("EMAIL SENT TO: " + dto.getEmail());
        return ResponseEntity.ok(Map.of(
                "message", "Nese email ekziston, reset link eshte derguar."
        ));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.badRequest().body("Passwords nuk jane te njejta!");
        }

        authService.resetPassword(request.getToken(), request.getNewPassword(), request.getConfirmPassword());

        return ResponseEntity.ok(Map.of(
                "message", "Passwordi eshte ndryshuar me sukses."
        ));
    }

}

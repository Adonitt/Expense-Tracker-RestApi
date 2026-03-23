package org.example.incomeandexpensebackend.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.example.incomeandexpensebackend.dtos.auth.AuthResponseDto;
import org.example.incomeandexpensebackend.dtos.auth.ChangePasswordDto;
import org.example.incomeandexpensebackend.dtos.auth.LoginDto;
import org.example.incomeandexpensebackend.dtos.user.CreateUserDto;
import org.example.incomeandexpensebackend.services.implementations.AuthServiceImpl;
import org.example.incomeandexpensebackend.services.interfaces.AuthService;
import org.example.incomeandexpensebackend.services.interfaces.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        return ResponseEntity.ok("Password changed successfully");
    }

}

package org.example.incomeandexpensebackend.services.implementations;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.incomeandexpensebackend.dtos.auth.AuthResponseDto;
import org.example.incomeandexpensebackend.dtos.auth.ChangePasswordDto;
import org.example.incomeandexpensebackend.dtos.auth.LoginDto;
import org.example.incomeandexpensebackend.entities.UserEntity;
import org.example.incomeandexpensebackend.exceptions.UnauthorizedException;
import org.example.incomeandexpensebackend.exceptions.UserNotFoundException;
import org.example.incomeandexpensebackend.repositories.UserRepository;
import org.example.incomeandexpensebackend.security.JWTUtil;
import org.example.incomeandexpensebackend.services.interfaces.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final HttpServletRequest httpServletRequest;


    @Override
    public AuthResponseDto login(LoginDto dto) {
        UserEntity user = userRepository.findByEmail(dto.getEmail()).orElseThrow(() -> new UserNotFoundException("User with this email doesn't exist!"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                String.valueOf(user.getRole())
        );

        return new AuthResponseDto(token, user.getId(), user.getEmail(), user.getFirstName());
    }

    @Override
    public UserEntity validateToken(String token) {
        String email = jwtUtil.validateTokenAndGetEmail(token);
        return userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Override
    public String getLoggedInUserEmail() {
        String authHeader = httpServletRequest.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);

        return jwtUtil.validateTokenAndGetEmail(token);
    }

    @Override
    public void changePassword(ChangePasswordDto req, String userEmail) {
        var userExists = userRepository.findByEmail(userEmail);

        if (userExists.isEmpty()) throw new UserNotFoundException("User not found");

        UserEntity user = userExists.get();

        if (!passwordEncoder.matches(req.getOldPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid old password");
        }

        if (!req.getNewPassword().equals(req.getConfirmPassword())) {
            throw new UnauthorizedException("New password and confirm password don't match!");
        }

        if (req.getNewPassword().equals(req.getOldPassword())) {
            throw new UnauthorizedException("New password cannot be the same as the old password!");
        }

        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public Long getLoggedInUserId() {
        String authHeader = httpServletRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        var claims = jwtUtil.decodeToken(token); // do t’i krijojmë
        Object id = claims.get("id");
        if (id == null) throw new UnauthorizedException("Invalid token: no userId");
        return Long.valueOf(id.toString());
    }

    @Override
    public String getLoggedInUserRole() {
        String authHeader = httpServletRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        var claims = jwtUtil.decodeToken(token);
        return claims.get("role", String.class);
    }
}

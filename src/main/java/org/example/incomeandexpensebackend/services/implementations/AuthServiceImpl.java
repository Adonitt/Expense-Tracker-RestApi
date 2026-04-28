package org.example.incomeandexpensebackend.services.implementations;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.incomeandexpensebackend.dtos.auth.AuthResponseDto;
import org.example.incomeandexpensebackend.dtos.auth.ChangePasswordDto;
import org.example.incomeandexpensebackend.dtos.auth.LoginDto;
import org.example.incomeandexpensebackend.entities.PasswordResetTokenEntity;
import org.example.incomeandexpensebackend.entities.UserEntity;
import org.example.incomeandexpensebackend.exceptions.UnauthorizedException;
import org.example.incomeandexpensebackend.exceptions.UserNotFoundException;
import org.example.incomeandexpensebackend.repositories.PasswordResetTokenRepository;
import org.example.incomeandexpensebackend.repositories.UserRepository;
import org.example.incomeandexpensebackend.security.JWTUtil;
import org.example.incomeandexpensebackend.services.interfaces.AuthService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final HttpServletRequest httpServletRequest;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final JavaMailSender javaMailSender;


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
                String.valueOf(user.getRole()),
                user.getIsActive()
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
        var claims = jwtUtil.decodeToken(token);
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

    public void forgotPassword(String email) {
        Optional<UserEntity> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) return;

        UserEntity user = userOpt.get();

        String token = UUID.randomUUID().toString();

        PasswordResetTokenEntity resetToken = new PasswordResetTokenEntity();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(1));

        passwordResetTokenRepository.save(resetToken);

        String link = "http://localhost:5173/reset-password?token=" + token;

        sendEmail(user.getEmail(), link);
    }

    private void sendEmail(String to, String link) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Reset Password");
            message.setText("Click here: " + link);

            javaMailSender.send(message);

            System.out.println("EMAIL SENT SUCCESSFULLY");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("EMAIL FAILED: " + e.getMessage());
        }
    }


    public void resetPassword(String token, String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            throw new RuntimeException("Passwords do not match");
        }

        PasswordResetTokenEntity resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expired");
        }

        UserEntity user = resetToken.getUser();

        user.setPassword(new BCryptPasswordEncoder().encode(newPassword));
        userRepository.save(user);

        passwordResetTokenRepository.delete(resetToken);
    }

}

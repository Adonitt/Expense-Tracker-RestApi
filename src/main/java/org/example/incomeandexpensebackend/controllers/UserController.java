package org.example.incomeandexpensebackend.controllers;

import lombok.RequiredArgsConstructor;
import org.example.incomeandexpensebackend.dtos.user.CreateUserDto;
import org.example.incomeandexpensebackend.dtos.user.UpdateUserDto;
import org.example.incomeandexpensebackend.dtos.user.UserDetailsDto;
import org.example.incomeandexpensebackend.dtos.user.UserListingDto;
import org.example.incomeandexpensebackend.entities.UserEntity;
import org.example.incomeandexpensebackend.enums.RoleEnum;
import org.example.incomeandexpensebackend.exceptions.UnauthorizedException;
import org.example.incomeandexpensebackend.services.interfaces.AuthService;
import org.example.incomeandexpensebackend.services.interfaces.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService service;
    private final AuthService authService;

    @GetMapping
    public ResponseEntity<List<UserListingDto>> getAllUsers() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDetailsDto> findById(@PathVariable Long id) {
        Long loggedInUserId = authService.getLoggedInUserId();
        String role = authService.getLoggedInUserRole();

        UserDetailsDto userDetails = service.findById(id);

        if (!role.equals("ADMIN") && !loggedInUserId.equals(id)) {
            throw new UnauthorizedException("You can only view your own data");
        }

        return ResponseEntity.ok(userDetails);
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<UpdateUserDto> update(@PathVariable Long id, @RequestBody UpdateUserDto updateUserDto) {
        return ResponseEntity.ok(service.update(id, updateUserDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeById(@PathVariable Long id) {
        service.removeById(id);
        return ResponseEntity.noContent().build();
    }

}

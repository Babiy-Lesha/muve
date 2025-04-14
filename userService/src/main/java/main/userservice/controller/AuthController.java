package main.userservice.controller;

import main.userservice.dto.AuthRequest;
import main.userservice.dto.AuthResponse;
import main.userservice.dto.UserCreateDto;
import main.userservice.dto.UserDto;
import main.userservice.entity.Role;
import main.userservice.service.CustomAuthService;
import main.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Аутентификация", description = "API для регистрации и авторизации пользователей")
public class AuthController {

    private final UserService userService;
    private final CustomAuthService authService;

    @Operation(summary = "Регистрация нового пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пользователь зарегистрирован"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные")
    })
    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody UserCreateDto registerDto) {
        if (registerDto.getRole() == null || registerDto.getRole().isEmpty()) {
            registerDto.setRole(Role.USER.name());
        }

        UserDto createdUser = userService.createUser(registerDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @Operation(summary = "Авторизация пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Авторизация успешна"),
            @ApiResponse(responseCode = "401", description = "Неверные учетные данные")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest loginDto) {
        // Используем кастомный сервис аутентификации
        AuthResponse authResponse = authService.authenticate(loginDto);
        return ResponseEntity.ok(authResponse);
    }
}
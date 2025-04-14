package main.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Данные для авторизации")
public class AuthRequest {

    @Schema(description = "Имя пользователя", example = "user1")
    @NotBlank(message = "Имя пользователя обязательно")
    private String username;

    @Schema(description = "Пароль", example = "password123")
    @NotBlank(message = "Пароль обязателен")
    private String password;
}
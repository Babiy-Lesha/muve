package main.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO для создания пользователя")
public class UserCreateDto {

    @Schema(description = "Имя пользователя", example = "user1")
    @NotBlank(message = "Имя пользователя обязательно")
    @Size(min = 3, max = 50, message = "Имя пользователя должно быть от 3 до 50 символов")
    private String username;

    @Schema(description = "Пароль", example = "password123")
    @NotBlank(message = "Пароль обязателен")
    @Size(min = 6, message = "Пароль должен быть не менее 6 символов")
    private String password;

    @Schema(description = "Email пользователя", example = "user@example.com")
    @NotBlank(message = "Email обязателен")
    @Email(message = "Некорректный формат email")
    private String email;

    @Schema(description = "Роль пользователя: USER или ADMIN", example = "USER", defaultValue = "USER")
    private String role;
}
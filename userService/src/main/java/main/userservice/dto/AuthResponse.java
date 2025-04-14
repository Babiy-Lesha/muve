package main.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Данные авторизации")
public class AuthResponse {

    @Schema(description = "JWT токен")
    private String token;

    @Schema(description = "Тип токена", example = "Bearer")
    private String tokenType;

    @Schema(description = "Имя пользователя")
    private String username;

    @Schema(description = "Роль пользователя")
    private String role;
}
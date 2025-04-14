package main.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.userservice.entity.Role;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Данные пользователя")
public class UserDto {

    @Schema(description = "Идентификатор пользователя")
    private Long id;

    @Schema(description = "Имя пользователя", example = "user1")
    private String username;

    @Schema(description = "Email пользователя", example = "user@example.com")
    private String email;

    @Schema(description = "Роль пользователя", example = "USER")
    private Role role;

    @Schema(description = "Дата создания")
    private LocalDateTime createdAt;

    @Schema(description = "Дата обновления")
    private LocalDateTime updatedAt;
}
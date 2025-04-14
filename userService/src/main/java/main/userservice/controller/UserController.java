package main.userservice.controller;

import main.userservice.dto.UserDto;
import main.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Пользователи", description = "API для управления пользователями")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Получение пользователя по ID",
            description = "Доступно администраторам или самому пользователю")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Данные найдены"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityUtils.isCurrentUser(#id)")
    public ResponseEntity<UserDto> getUserById(
            @Parameter(description = "ID пользователя", required = true)
            @PathVariable Long id) {
        UserDto user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Получение списка всех пользователей",
            description = "Доступно только администраторам")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список пользователей"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Удаление пользователя",
            description = "Доступно только администраторам")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Данные удаленного пользователя",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class)) }),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> deleteUser(
            @Parameter(description = "ID пользователя", required = true)
            @PathVariable Long id) {
        UserDto deletedUser = userService.deleteUser(id);
        return ResponseEntity.ok(deletedUser);
    }

    @Operation(summary = "Проверка существования пользователя по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Результат проверки")
    })
    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> checkUserExists(
            @Parameter(description = "ID пользователя", required = true)
            @PathVariable Long id) {
        boolean exists = userService.existsById(id);
        return ResponseEntity.ok(exists);
    }
}
package main.userservice.controller;

import main.userservice.dto.UserMovieDto;
import main.userservice.service.UserMovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/movies")
@RequiredArgsConstructor
@Tag(name = "Коллекции фильмов пользователей",
        description = "API для управления личными коллекциями фильмов")
public class UserMovieController {

    private final UserMovieService userMovieService;

    @Operation(summary = "Добавление фильма в коллекцию пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Фильм добавлен в коллекцию"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "404", description = "Пользователь или фильм не найден"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "503", description = "Сервис фильмов недоступен")
    })
    @PostMapping("/{movieId}")
    @PreAuthorize("hasRole('ADMIN') or @securityUtils.isCurrentUser(#userId)")
    public ResponseEntity<UserMovieDto> addMovieToCollection(
            @Parameter(description = "ID пользователя", required = true)
            @PathVariable Long userId,
            @Parameter(description = "ID фильма", required = true)
            @PathVariable Long movieId,
            @Parameter(description = "Примечание к фильму")
            @RequestParam(required = false) String note) {

        UserMovieDto userMovieDto = userMovieService.addMovieToCollectionWithNote(userId, movieId, note);
        return ResponseEntity.status(HttpStatus.CREATED).body(userMovieDto);
    }

    @Operation(summary = "Получение всех фильмов из коллекции пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список фильмов"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or @securityUtils.isCurrentUser(#userId)")
    public ResponseEntity<List<UserMovieDto>> getUserMovies(
            @Parameter(description = "ID пользователя", required = true)
            @PathVariable Long userId) {

        List<UserMovieDto> userMovies = userMovieService.getUserMovies(userId);
        return ResponseEntity.ok(userMovies);
    }

    @Operation(summary = "Оценка фильма",
            description = "Установка оценки фильму от 1 до 5")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Оценка установлена"),
            @ApiResponse(responseCode = "400", description = "Некорректная оценка"),
            @ApiResponse(responseCode = "404", description = "Пользователь или фильм не найден"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "503", description = "Сервис фильмов недоступен")
    })
    @PostMapping("/{movieId}/rate")
    @PreAuthorize("hasRole('ADMIN') or @securityUtils.isCurrentUser(#userId)")
    public ResponseEntity<UserMovieDto> rateMovie(
            @Parameter(description = "ID пользователя", required = true)
            @PathVariable Long userId,
            @Parameter(description = "ID фильма", required = true)
            @PathVariable Long movieId,
            @Parameter(description = "Оценка от 1 до 5", required = true)
            @RequestParam @Min(1) @Max(5) Integer rating) {

        UserMovieDto ratedMovie = userMovieService.rateMovie(userId, movieId, rating);
        return ResponseEntity.ok(ratedMovie);
    }

    @Operation(summary = "Удаление фильма из коллекции пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Фильм удален из коллекции"),
            @ApiResponse(responseCode = "404", description = "Фильм не найден в коллекции"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    @DeleteMapping("/{movieId}")
    @PreAuthorize("hasRole('ADMIN') or @securityUtils.isCurrentUser(#userId)")
    public ResponseEntity<Void> removeMovieFromCollection(
            @Parameter(description = "ID пользователя", required = true)
            @PathVariable Long userId,
            @Parameter(description = "ID фильма", required = true)
            @PathVariable Long movieId) {

        userMovieService.removeMovieFromCollection(userId, movieId);
        return ResponseEntity.noContent().build();
    }

}
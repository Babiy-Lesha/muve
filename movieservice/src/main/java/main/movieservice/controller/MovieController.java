package main.movieservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import main.movieservice.dto.MovieCreateDto;
import main.movieservice.dto.MovieDto;
import main.movieservice.service.MovieService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
@Tag(name = "Фильмы", description = "API для управления фильмами")
public class MovieController {

    private final MovieService movieService;

    @Operation(summary = "Получение фильма по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Фильм найден"),
            @ApiResponse(responseCode = "404", description = "Фильм не найден")
    })
    @GetMapping("/{id}")
    public ResponseEntity<MovieDto> getMovieById(
            @Parameter(description = "ID фильма", required = true)
            @PathVariable Long id) {
        MovieDto movie = movieService.getMovieById(id);
        return ResponseEntity.ok(movie);
    }

    @Operation(summary = "Проверка существования фильма по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Результат проверки")
    })
    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> checkMovieExists(
            @Parameter(description = "ID фильма", required = true)
            @PathVariable Long id) {
        boolean exists = movieService.existsById(id);
        return ResponseEntity.ok(exists);
    }

    @Operation(summary = "Получение списка всех фильмов")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список фильмов")
    })
    @GetMapping
    public ResponseEntity<List<MovieDto>> getAllMovies() {
        List<MovieDto> movies = movieService.getAllMovies();
        return ResponseEntity.ok(movies);
    }

    @Operation(summary = "Поиск фильмов по ключевому слову")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список найденных фильмов")
    })
    @GetMapping("/search")
    public ResponseEntity<List<MovieDto>> searchMovies(
            @Parameter(description = "Ключевое слово для поиска")
            @RequestParam(required = false) String keyword) {
        List<MovieDto> movies = movieService.searchMovies(keyword);
        return ResponseEntity.ok(movies);
    }

    @Operation(summary = "Создание нового фильма",
            description = "Доступно только для администраторов")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Фильм создан успешно",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MovieDto.class)) }),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MovieDto> createMovie(
            @Parameter(description = "Данные фильма", required = true)
            @Valid @RequestBody MovieCreateDto dto) {
        MovieDto createdMovie = movieService.createMovie(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMovie);
    }

    @Operation(summary = "Обновление фильма",
            description = "Доступно только для администраторов")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Фильм обновлен"),
            @ApiResponse(responseCode = "404", description = "Фильм не найден"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MovieDto> updateMovie(
            @Parameter(description = "ID фильма", required = true)
            @PathVariable Long id,
            @Parameter(description = "Обновленные данные фильма", required = true)
            @Valid @RequestBody MovieDto dto) {
        MovieDto updatedMovie = movieService.updateMovie(id, dto);
        return ResponseEntity.ok(updatedMovie);
    }

    @Operation(summary = "Удаление фильма",
            description = "Доступно только для администраторов")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Фильм удален"),
            @ApiResponse(responseCode = "404", description = "Фильм не найден"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMovie(
            @Parameter(description = "ID фильма", required = true)
            @PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }
}
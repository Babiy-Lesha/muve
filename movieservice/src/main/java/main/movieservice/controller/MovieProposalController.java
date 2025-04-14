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
import main.movieservice.dto.MovieProposalDto;
import main.movieservice.entity.ProposalStatus;
import main.movieservice.service.MovieProposalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proposals")
@RequiredArgsConstructor
@Tag(name = "Предложения фильмов", description = "API для управления предложениями фильмов")
public class MovieProposalController {

    private final MovieProposalService movieProposalService;

    @Operation(summary = "Получение всех предложений",
            description = "Доступно только для администраторов")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список предложений"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MovieProposalDto>> getAllProposals() {
        List<MovieProposalDto> proposals = movieProposalService.getAllProposals();
        return ResponseEntity.ok(proposals);
    }

    @Operation(summary = "Получение предложений по статусу",
            description = "Доступно только для администраторов")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список предложений"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MovieProposalDto>> getProposalsByStatus(
            @Parameter(description = "Статус предложения (PENDING, APPROVED, REJECTED)", required = true)
            @PathVariable ProposalStatus status) {
        List<MovieProposalDto> proposals = movieProposalService.getProposalsByStatus(status);
        return ResponseEntity.ok(proposals);
    }

    @Operation(summary = "Создание предложения фильма")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Предложение создано",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MovieProposalDto.class)) }),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @PostMapping
    public ResponseEntity<MovieProposalDto> createProposal(
            @Parameter(description = "Данные предложения", required = true)
            @Valid @RequestBody MovieProposalDto dto) {
        MovieProposalDto createdProposal = movieProposalService.createProposal(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProposal);
    }

    @Operation(summary = "Одобрение предложения фильма",
            description = "Доступно только для администраторов")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Предложение одобрено",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MovieProposalDto.class)) }),
            @ApiResponse(responseCode = "404", description = "Предложение не найдено"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MovieProposalDto> approveProposal(
            @Parameter(description = "ID предложения", required = true)
            @PathVariable Long id,
            @Parameter(description = "Комментарий администратора")
            @RequestParam(required = false) String adminComment) {
        MovieProposalDto approvedProposal = movieProposalService.approveProposal(id, adminComment);
        return ResponseEntity.ok(approvedProposal);
    }

    @Operation(summary = "Отклонение предложения фильма",
            description = "Доступно только для администраторов")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Предложение отклонено",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MovieProposalDto.class)) }),
            @ApiResponse(responseCode = "404", description = "Предложение не найдено"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MovieProposalDto> rejectProposal(
            @Parameter(description = "ID предложения", required = true)
            @PathVariable Long id,
            @Parameter(description = "Комментарий администратора")
            @RequestParam(required = false) String adminComment) {
        MovieProposalDto rejectedProposal = movieProposalService.rejectProposal(id, adminComment);
        return ResponseEntity.ok(rejectedProposal);
    }
}
package main.movieservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.movieservice.entity.ProposalStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Данные о предложении фильма")
public class MovieProposalDto {

    @Schema(description = "Идентификатор предложения")
    private Long id;

    @Schema(description = "Идентификатор пользователя")
    private Long userId;

    @Schema(description = "Название фильма", example = "Зеленая миля")
    @NotBlank(message = "Название фильма обязательно")
    private String title;

    @Schema(description = "Описание фильма")
    private String description;

    @Schema(description = "Жанр фильма", example = "Драма")
    private String genre;

    @Schema(description = "Статус предложения", example = "PENDING")
    private ProposalStatus status;

    @Schema(description = "Комментарий администратора")
    private String adminComment;
}
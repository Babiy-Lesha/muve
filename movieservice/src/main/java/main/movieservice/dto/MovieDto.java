package main.movieservice.dto;

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
@Schema(description = "Данные о фильме")
public class MovieDto {

    @Schema(description = "Идентификатор фильма")
    private Long id;

    @Schema(description = "Название фильма", example = "Побег из Шоушенка")
    @NotBlank(message = "Название фильма обязательно")
    private String title;

    @Schema(description = "Описание фильма")
    private String description;

    @Schema(description = "Жанр фильма", example = "Драма")
    private String genre;

    @Schema(description = "Средний рейтинг фильма", example = "4.7")
    private Double rating;
}
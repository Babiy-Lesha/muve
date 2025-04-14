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
@Schema(description = "Данные об оценке фильма")
public class MovieRatingDto {

    @Schema(description = "Идентификатор пользователя")
    private Long userId;

    @Schema(description = "Идентификатор фильма")
    private Long movieId;

    @Schema(description = "Оценка фильма (от 1 до 5)")
    private Integer rating;
}
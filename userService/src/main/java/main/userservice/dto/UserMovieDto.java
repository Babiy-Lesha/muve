package main.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Фильм в коллекции пользователя")
public class UserMovieDto {

    @Schema(description = "Идентификатор записи")
    private Long id;

    @Schema(description = "Идентификатор пользователя")
    private Long userId;

    @Schema(description = "Идентификатор фильма")
    private Long movieId;

    @Schema(description = "Оценка фильма (от 1 до 5)")
    @Min(value = 1, message = "Оценка должна быть от 1 до 5")
    @Max(value = 5, message = "Оценка должна быть от 1 до 5")
    private Integer rating;

    @Schema(description = "Примечание пользователя")
    private String note;

    @Schema(description = "Фильм добавлен в коллекцию")
    private boolean addedToCollection;

    @Schema(description = "Дата добавления")
    private LocalDateTime createdAt;

    @Schema(description = "Дата обновления")
    private LocalDateTime updatedAt;
}
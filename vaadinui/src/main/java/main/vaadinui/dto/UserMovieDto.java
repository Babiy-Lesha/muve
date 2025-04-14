package main.vaadinui.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMovieDto {
    private Long id;
    private Long userId;
    private Long movieId;
    private Integer rating;
    private String note;
    private boolean addedToCollection;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Дополнительные поля для UI
    private String movieTitle;
    private String movieGenre;
}
package main.vaadinui.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieRatingDto {
    private Long userId;
    private Long movieId;
    private Integer rating;
}
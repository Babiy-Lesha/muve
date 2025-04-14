package main.vaadinui.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieProposalDto {
    private Long id;
    private Long userId;
    private String title;
    private String description;
    private String genre;
    private String status;
    private String adminComment;
}
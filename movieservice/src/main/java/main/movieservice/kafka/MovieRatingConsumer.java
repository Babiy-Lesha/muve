package main.movieservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import main.movieservice.dto.MovieRatingDto;
import main.movieservice.service.MovieRatingService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MovieRatingConsumer {

    private final MovieRatingService movieRatingService;

    @KafkaListener(
            topics = "movie-ratings",
            groupId = "movie-service-group",
            containerFactory = "movieRatingKafkaListenerContainerFactory"
    )
    public void consumeRating(MovieRatingDto ratingDto) {
        //Получена оценка фильма
        movieRatingService.processRating(ratingDto);
    }
}
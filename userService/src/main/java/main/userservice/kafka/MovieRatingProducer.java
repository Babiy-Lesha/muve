package main.userservice.kafka;

import main.userservice.dto.MovieRatingDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MovieRatingProducer {

    private final KafkaTemplate<String, MovieRatingDto> kafkaTemplate;
    private static final String TOPIC = "movie-ratings";

    public void sendMovieRating(MovieRatingDto rating) {
        kafkaTemplate.send(TOPIC, rating.getMovieId().toString(), rating)
                .thenAccept(result -> log.info("Rating sent successfully to topic {}", TOPIC))
                .exceptionally(ex -> {
                    log.error("Failed to send rating to topic {}: {}", TOPIC, ex.getMessage());
                    return null;
                });
    }
}
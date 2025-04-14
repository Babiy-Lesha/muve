package main.userservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import main.userservice.dto.MovieDeletedEventDto;
import main.userservice.service.UserMovieService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MovieEventConsumer {

    private final UserMovieService userMovieService;

    @KafkaListener(
            topics = "movie-events",
            groupId = "user-service-group",
            containerFactory = "movieEventKafkaListenerContainerFactory"
    )
    public void consumeMovieDeletedEvent(MovieDeletedEventDto event) {
        log.info("Получено событие удаления фильма: {}", event);
        userMovieService.removeMovieFromAllCollections(event.getMovieId());
    }
}
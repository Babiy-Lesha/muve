package main.movieservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import main.movieservice.dto.MovieDeletedEventDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MovieEventProducer {

    private final KafkaTemplate<String, MovieDeletedEventDto> kafkaTemplate;
    private static final String TOPIC = "movie-events";

    public void sendMovieDeletedEvent(MovieDeletedEventDto event) {
        kafkaTemplate.send(TOPIC, event.getMovieId().toString(), event)
                .thenAccept(result -> log.info("Событие удаления фильма успешно отправлено в топик {}", TOPIC))
                .exceptionally(ex -> {
                    log.error("Ошибка отправки события удаления фильма в топик {}: {}", TOPIC, ex.getMessage());
                    return null;
                });
    }
}
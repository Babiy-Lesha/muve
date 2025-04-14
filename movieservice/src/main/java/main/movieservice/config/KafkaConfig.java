package main.movieservice.config;

import main.movieservice.dto.MovieDeletedEventDto;
import main.movieservice.dto.MovieProposalDto;
import main.movieservice.dto.MovieRatingDto;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    /**
     * Определяем топик для предложений фильмов
     */
    @Bean
    public NewTopic movieProposalTopic() {
        return new NewTopic("movie-proposals", 1, (short) 1);
    }

    /**
     * Добавляем топик для рейтингов фильмов
     */
    @Bean
    public NewTopic movieRatingsTopic() {
        return new NewTopic("movie-ratings", 1, (short) 1);
    }

    /**
     *  Добавляем топик для событий фильмов
     */
    @Bean
    public NewTopic movieEventsTopic() {
        return new NewTopic("movie-events", 1, (short) 1);
    }

    // Конфигурация админа Kafka для создания топиков
    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
    }

    // Фабрика для продюсера предложений фильмов
    @Bean
    public ProducerFactory<String, MovieProposalDto> movieProposalProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(JsonSerializer.TYPE_MAPPINGS, "proposal:main.movieservice.dto.MovieProposalDto");
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    // Template для отправки предложений фильмов
    @Bean
    public KafkaTemplate<String, MovieProposalDto> movieProposalKafkaTemplate() {
        return new KafkaTemplate<>(movieProposalProducerFactory());
    }

    // Фабрика для потребителя оценок фильмов
    @Bean
    public ConsumerFactory<String, MovieRatingDto> movieRatingConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "movie-service-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "main.movieservice.dto,main.userservice.dto");
        props.put(JsonDeserializer.TYPE_MAPPINGS, "rating:main.movieservice.dto.MovieRatingDto");

        return new DefaultKafkaConsumerFactory<>(props);
    }

    // Listener контейнер для оценок фильмов
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MovieRatingDto> movieRatingKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, MovieRatingDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(movieRatingConsumerFactory());
        return factory;
    }

    // Добавляем фабрику для продюсера событий удаления фильмов
    @Bean
    public ProducerFactory<String, MovieDeletedEventDto> movieEventProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(JsonSerializer.TYPE_MAPPINGS, "movieDeleted:main.movieservice.dto.MovieDeletedEventDto");
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    // Добавляем KafkaTemplate для событий удаления фильмов
    @Bean
    public KafkaTemplate<String, MovieDeletedEventDto> movieEventKafkaTemplate() {
        return new KafkaTemplate<>(movieEventProducerFactory());
    }
}
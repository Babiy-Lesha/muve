package main.userservice.config;

import main.userservice.dto.MovieDeletedEventDto;
import main.userservice.dto.MovieRatingDto;
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

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic movieRatingsTopic() {
        return new NewTopic("movie-ratings", 1, (short) 1);
    }

    @Bean
    public ProducerFactory<String, MovieRatingDto> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(JsonSerializer.TYPE_MAPPINGS, "rating:main.userservice.dto.MovieRatingDto");
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, MovieRatingDto> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    // Фабрика для потребителя событий фильмов
    @Bean
    public ConsumerFactory<String, MovieDeletedEventDto> movieEventConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "user-service-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "main.movieservice.dto,main.userservice.dto");
        props.put(JsonDeserializer.TYPE_MAPPINGS, "movieDeleted:main.userservice.dto.MovieDeletedEventDto");

        return new DefaultKafkaConsumerFactory<>(props);
    }

    // Listener контейнер для событий фильмов
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MovieDeletedEventDto> movieEventKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, MovieDeletedEventDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(movieEventConsumerFactory());
        return factory;
    }
}
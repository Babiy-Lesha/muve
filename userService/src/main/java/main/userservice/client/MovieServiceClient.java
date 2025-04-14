package main.userservice.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import main.userservice.exception.ServiceUnavailableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MovieServiceClient {
    private static final int REQUEST_TIMEOUT_MS = 5000;

    private final WebClient.Builder webClientBuilder;
    private final DiscoveryClient discoveryClient;

    @Value("${api.gateway.name}")
    private String apiGatewayName;

    @Value("${api.gateway.port}")
    private int apiGatewayPort;

    public boolean checkMovieExists(Long movieId) {
        // Проверяем, зарегистрирован ли API Gateway в Eureka
        if (!isApiGatewayAvailable()) {
            throw new ServiceUnavailableException("Сервис API Gateway недоступен. Пожалуйста, попробуйте позже.");
        }

        try {
            String url = String.format("http://%s:%d/api/movies/%d/exists", apiGatewayName, apiGatewayPort, movieId);

            return Optional.ofNullable(webClientBuilder.build()
                    .get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .timeout(Duration.ofMillis(REQUEST_TIMEOUT_MS))
                    .onErrorMap(ex -> new ServiceUnavailableException("Сервис фильмов временно недоступен.", ex))
                    .block())
                    .orElse(false);
        } catch (WebClientResponseException e) {
            throw new ServiceUnavailableException("Сервис фильмов временно недоступен.");
        }
    }

    private boolean isApiGatewayAvailable() {
        boolean available = !discoveryClient.getInstances(apiGatewayName).isEmpty();
        if (!available) {
            log.warn("API Gateway ({}) is not registered in Eureka", apiGatewayName);
        }
        return available;
    }
}
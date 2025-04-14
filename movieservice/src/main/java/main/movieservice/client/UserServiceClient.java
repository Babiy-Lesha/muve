package main.movieservice.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class UserServiceClient {
    private static final int REQUEST_TIMEOUT_MS = 5000;

    private final WebClient.Builder webClientBuilder;
    private final DiscoveryClient discoveryClient;

    @Value("${api.gateway.name}")
    private String apiGatewayName;

    @Value("${api.gateway.port}")
    private int apiGatewayPort;

    public boolean checkUserExists(Long userId) {
        if (!isApiGatewayAvailable()) {
            return true;
        }

        try {
            String url = String.format("http://%s:%d/api/users/%d/exists", apiGatewayName, apiGatewayPort, userId);

            return Optional.ofNullable(webClientBuilder.build()
                    .get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .timeout(Duration.ofMillis(REQUEST_TIMEOUT_MS))
                    .onErrorResume(ex -> {
                        System.out.println();
                        log.error("Failed to check user existence: {}", ex.getMessage());
                        // В случае ошибки позволяем операции продолжиться
                        return reactor.core.publisher.Mono.just(true);
                    })
                    .block())
                    .orElse(false);
        } catch (WebClientResponseException e) {
            return true;
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
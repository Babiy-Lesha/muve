package main.vaadinui.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

@Configuration
@Slf4j
public class RestClientConfig {

    @Value("${api.gateway.url}")
    private String apiGatewayUrl;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl(apiGatewayUrl)
                .build();
    }
}
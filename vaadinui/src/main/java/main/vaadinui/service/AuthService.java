package main.vaadinui.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import main.vaadinui.dto.AuthRequest;
import main.vaadinui.dto.AuthResponse;
import main.vaadinui.dto.UserCreateDto;
import main.vaadinui.dto.UserDto;
import main.vaadinui.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final RestClient restClient;

    public AuthResponse login(String username, String password) {
        try {
            AuthRequest request = new AuthRequest(username, password);

            return restClient.post()
                    .uri("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(AuthResponse.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw ApiException.unauthorized();
            }
            throw ApiException.serverError("Ошибка при аутентификации: " + e.getMessage());
        }
    }

    public UserDto register(UserCreateDto userCreateDto) {
        try {
            return restClient.post()
                    .uri("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(userCreateDto)
                    .retrieve()
                    .body(UserDto.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw ApiException.badRequest("Ошибка регистрации: " + e.getMessage());
            }
            throw ApiException.serverError("Ошибка при регистрации: " + e.getMessage());
        }
    }
}
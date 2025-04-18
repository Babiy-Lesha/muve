package main.vaadinui.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import main.vaadinui.dto.UserDto;
import main.vaadinui.exception.ApiException;
import main.vaadinui.security.SecurityService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final RestClient restClient;
    private final SecurityService securityService;

    public List<UserDto> getAllUsers() {
        try {
            return restClient.get()
                    .uri("/api/users")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + securityService.getCurrentUser().getToken())
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<UserDto>>() {
                    });
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                throw ApiException.forbidden();
            }
            return Collections.emptyList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public UserDto getUserById(Long userId) {
        try {
            return restClient.get()
                    .uri("/api/users/{userId}", userId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + securityService.getCurrentUser().getToken())
                    .retrieve()
                    .body(UserDto.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                throw ApiException.forbidden();
            } else if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw ApiException.notFound("Пользователь не найден");
            }
            throw ApiException.serverError("Ошибка при получении пользователя: " + e.getMessage());
        } catch (Exception e) {
            throw ApiException.serverError("Неожиданная ошибка при получении пользователя: " + e.getMessage());
        }
    }

    public void deleteUser(Long userId) {
        try {
            restClient.delete()
                    .uri("/api/users/{userId}", userId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + securityService.getCurrentUser().getToken())
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                throw ApiException.forbidden();
            } else if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw ApiException.notFound("Пользователь не найден");
            }
            throw ApiException.serverError("Ошибка при удалении пользователя: " + e.getMessage());
        } catch (Exception e) {
            throw ApiException.serverError("Неожиданная ошибка при удалении пользователя: " + e.getMessage());
        }
    }

    public UserDto getUserByUsername(String username) {
        return restClient.get()
                .uri("/api/users/by-username/{username}")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + securityService.getCurrentUser().getToken())
                .retrieve()
                .body(UserDto.class);
    }
}
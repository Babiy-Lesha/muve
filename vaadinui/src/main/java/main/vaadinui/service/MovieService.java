package main.vaadinui.service;

import lombok.RequiredArgsConstructor;
import main.vaadinui.dto.MovieCreateDto;
import main.vaadinui.dto.MovieDto;
import main.vaadinui.exception.ApiException;
import main.vaadinui.security.SecurityService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final RestClient restClient;
    private final SecurityService securityService;

    public List<MovieDto> getAllMovies() {
        try {
            return restClient.get()
                    .uri("/api/movies")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + securityService.getCurrentUser().getToken())
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<MovieDto>>() {
                    });
        } catch (HttpClientErrorException e) {
            return Collections.emptyList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public List<MovieDto> searchMovies(String keyword) {
        try {
            return restClient.get()
                    .uri("/api/movies/search?keyword={keyword}", keyword)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + securityService.getCurrentUser().getToken())
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<MovieDto>>() {
                    });
        } catch (HttpClientErrorException e) {
            return Collections.emptyList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public MovieDto getMovieById(Long id) {
        try {
            return restClient.get()
                    .uri("/api/movies/{id}", id)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + securityService.getCurrentUser().getToken())
                    .retrieve()
                    .body(MovieDto.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw ApiException.notFound("Фильм не найден");
            }
            throw ApiException.serverError("Ошибка при получении фильма: " + e.getMessage());
        }
    }

    public MovieDto createMovie(MovieCreateDto movieCreateDto) {
        try {
            return restClient.post()
                    .uri("/api/movies")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + securityService.getCurrentUser().getToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(movieCreateDto)
                    .retrieve()
                    .body(MovieDto.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                throw ApiException.forbidden();
            } else if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw ApiException.badRequest("Ошибка при создании фильма: " + e.getMessage());
            }
            throw ApiException.serverError("Ошибка при создании фильма: " + e.getMessage());
        }
    }

    public MovieDto updateMovie(Long id, MovieDto movieDto) {
        try {
            return restClient.put()
                    .uri("/api/movies/{id}", id)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + securityService.getCurrentUser().getToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(movieDto)
                    .retrieve()
                    .body(MovieDto.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                throw ApiException.forbidden();
            }
            throw ApiException.serverError("Ошибка при обновлении фильма: " + e.getMessage());
        }
    }

    public void deleteMovie(Long id) {
        try {
            restClient.delete()
                    .uri("/api/movies/{id}", id)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + securityService.getCurrentUser().getToken())
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                throw ApiException.forbidden();
            }
            throw ApiException.serverError("Ошибка при удалении фильма: " + e.getMessage());
        }
    }
}
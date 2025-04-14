package main.vaadinui.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import main.vaadinui.dto.MovieRatingDto;
import main.vaadinui.dto.UserMovieDto;
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
@Slf4j
public class UserMovieService {

    private final RestClient restClient;
    private final SecurityService securityService;
    private final MovieService movieService;

    public List<UserMovieDto> getUserMovies(Long userId) {
        try {
            List<UserMovieDto> userMovies = restClient.get()
                    .uri("/api/users/{userId}/movies", userId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + securityService.getCurrentUser().getToken())
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<UserMovieDto>>() {});

            // Обогатим данные информацией о фильмах
            return enrichUserMoviesWithMovieInfo(userMovies);
        } catch (HttpClientErrorException e) {
            log.error("Ошибка при получении фильмов пользователя", e);
            if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                throw ApiException.forbidden();
            }
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("Неожиданная ошибка при получении фильмов пользователя", e);
            return Collections.emptyList();
        }
    }

    private List<UserMovieDto> enrichUserMoviesWithMovieInfo(List<UserMovieDto> userMovies) {
        if (userMovies == null) return Collections.emptyList();

        for (UserMovieDto userMovie : userMovies) {
            try {
                var movie = movieService.getMovieById(userMovie.getMovieId());
                userMovie.setMovieTitle(movie.getTitle());
                userMovie.setMovieGenre(movie.getGenre());
            } catch (Exception e) {
                log.warn("Не удалось получить информацию о фильме с id: {}", userMovie.getMovieId());
                userMovie.setMovieTitle("Неизвестный фильм");
            }
        }

        return userMovies;
    }

    public UserMovieDto addMovieToCollection(Long userId, Long movieId, String note) {
        try {
            return restClient.post()
                    .uri("/api/users/{userId}/movies/{movieId}?note={note}", userId, movieId, note)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + securityService.getCurrentUser().getToken())
                    .retrieve()
                    .body(UserMovieDto.class);
        } catch (HttpClientErrorException e) {
            log.error("Ошибка при добавлении фильма в коллекцию", e);
            if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                throw ApiException.forbidden();
            } else if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw ApiException.notFound("Фильм или пользователь не найден");
            }
            throw ApiException.serverError("Ошибка при добавлении фильма в коллекцию: " + e.getMessage());
        } catch (Exception e) {
            log.error("Неожиданная ошибка при добавлении фильма в коллекцию", e);
            throw ApiException.serverError("Неожиданная ошибка при добавлении фильма в коллекцию: " + e.getMessage());
        }
    }

    public UserMovieDto rateMovie(Long userId, Long movieId, Integer rating) {
        try {
            return restClient.post()
                    .uri("/api/users/{userId}/movies/{movieId}/rate?rating={rating}", userId, movieId, rating)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + securityService.getCurrentUser().getToken())
                    .retrieve()
                    .body(UserMovieDto.class);
        } catch (HttpClientErrorException e) {
            log.error("Ошибка при оценке фильма", e);
            if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                throw ApiException.forbidden();
            } else if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw ApiException.notFound("Фильм или пользователь не найден");
            } else if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw ApiException.badRequest("Некорректная оценка");
            }
            throw ApiException.serverError("Ошибка при оценке фильма: " + e.getMessage());
        } catch (Exception e) {
            log.error("Неожиданная ошибка при оценке фильма", e);
            throw ApiException.serverError("Неожиданная ошибка при оценке фильма: " + e.getMessage());
        }
    }

    public void removeMovieFromCollection(Long userId, Long movieId) {
        try {
            restClient.delete()
                    .uri("/api/users/{userId}/movies/{movieId}", userId, movieId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + securityService.getCurrentUser().getToken())
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException e) {
            log.error("Ошибка при удалении фильма из коллекции", e);
            if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                throw ApiException.forbidden();
            } else if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw ApiException.notFound("Фильм не найден в коллекции");
            }
            throw ApiException.serverError("Ошибка при удалении фильма из коллекции: " + e.getMessage());
        } catch (Exception e) {
            log.error("Неожиданная ошибка при удалении фильма из коллекции", e);
            throw ApiException.serverError("Неожиданная ошибка при удалении фильма из коллекции: " + e.getMessage());
        }
    }
}
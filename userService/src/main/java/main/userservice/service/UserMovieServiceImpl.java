package main.userservice.service;

import lombok.extern.slf4j.Slf4j;
import main.userservice.client.MovieServiceClient;
import main.userservice.dto.MovieRatingDto;
import main.userservice.dto.UserMovieDto;
import main.userservice.entity.User;
import main.userservice.entity.UserMovie;
import main.userservice.exception.ResourceNotFoundException;
import main.userservice.kafka.MovieRatingProducer;
import main.userservice.mapper.UserMovieMapper;
import main.userservice.repository.UserMovieRepository;
import main.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserMovieServiceImpl implements UserMovieService {

    private final UserRepository userRepository;
    private final UserMovieRepository userMovieRepository;
    private final UserMovieMapper userMovieMapper;
    private final MovieServiceClient movieServiceClient;
    private final MovieRatingProducer movieRatingProducer;

    @Override
    @Transactional
    public UserMovieDto addMovieToCollectionWithNote(Long userId, Long movieId, String note) {
        User user = getUserOrThrow(userId);

        // Проверяем существование фильма через http запрос к Movie Service
        boolean movieExists = movieServiceClient.checkMovieExists(movieId);
        if (!movieExists) {
            throw new ResourceNotFoundException(String.format("Фильм не найден с id: %d", movieId));
        }

        // Проверяем, есть ли уже этот фильм в коллекции пользователя
        if (userMovieRepository.existsByUserIdAndMovieId(userId, movieId)) {
            UserMovie existingUserMovie = userMovieRepository.findByUserIdAndMovieId(userId, movieId)
                    .orElseThrow(() -> new IllegalStateException("фильм существует"));

            // Если фильм уже был удален из коллекции, добавляем его снова
            if (!existingUserMovie.isAddedToCollection()) {
                existingUserMovie.setAddedToCollection(true);
                existingUserMovie.setNote(note);
                return userMovieMapper.toDto(userMovieRepository.save(existingUserMovie));
            }

            throw new IllegalArgumentException("Фильм уже находится в коллекции пользователя");
        }

        UserMovie userMovie = UserMovie.builder()
                .user(user)
                .movieId(movieId)
                .note(note)
                .addedToCollection(true)
                .build();

        UserMovie savedUserMovie = userMovieRepository.save(userMovie);
        return userMovieMapper.toDto(savedUserMovie);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserMovieDto> getUserMovies(Long userId) {
        getUserOrThrow(userId);

        List<UserMovie> userMovies = userMovieRepository.findByUserIdAndAddedToCollectionTrue(userId);
        return userMovieMapper.toDtoList(userMovies);
    }

    @Override
    @Transactional
    public UserMovieDto rateMovie(Long userId, Long movieId, Integer rating) {
        // Проверка рейтинга на допустимый диапазон
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Оценка должна быть от 1 до 5");
        }

        getUserOrThrow(userId);

        // Проверяем существование фильма через http запрос к Movie Service
        boolean movieExists = movieServiceClient.checkMovieExists(movieId);
        if (!movieExists) {
            throw new ResourceNotFoundException(String.format("Фильм не найден с id: %d", movieId));
        }

        // Проверяем, есть ли фильм в коллекции пользователя
        UserMovie userMovie = userMovieRepository.findByUserIdAndMovieId(userId, movieId)
                .orElseGet(() -> {
                    // Если фильма нет в коллекции, добавляем его автоматически
                    User user = getUserOrThrow(userId);

                    return UserMovie.builder()
                            .user(user)
                            .movieId(movieId)
                            .addedToCollection(true)
                            .build();
                });

        userMovie.setRating(rating);
        UserMovie updatedUserMovie = userMovieRepository.save(userMovie);

        // Отправляем событие в Kafka
        MovieRatingDto ratingDto = MovieRatingDto.builder()
                .userId(userId)
                .movieId(movieId)
                .rating(rating)
                .build();

        movieRatingProducer.sendMovieRating(ratingDto);
        return userMovieMapper.toDto(updatedUserMovie);
    }

    @Override
    @Transactional
    public void removeMovieFromCollection(Long userId, Long movieId) {
        UserMovie userMovie = userMovieRepository.findByUserIdAndMovieId(userId, movieId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Фильм с id: %d не найден в коллекции пользователя с id: %d", movieId, userId)));

        userMovie.setAddedToCollection(false);
        userMovieRepository.save(userMovie);
    }

    @Override
    @Transactional
    public void removeMovieFromAllCollections(Long movieId) {
        List<UserMovie> userMovies = userMovieRepository.findByMovieId(movieId);

        for (UserMovie userMovie : userMovies) {
            userMovie.setAddedToCollection(false);
        }

        userMovieRepository.saveAll(userMovies);
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Пользователь не найден с id: %d", userId)));
    }
}

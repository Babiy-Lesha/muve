package main.movieservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import main.movieservice.dto.MovieRatingDto;
import main.movieservice.exception.ResourceNotFoundException;
import main.movieservice.repository.MovieRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieRatingServiceImpl implements MovieRatingService {

    private final MovieRepository movieRepository;
    private final MovieService movieService;
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void processRating(MovieRatingDto ratingDto) {
        movieRepository.findById(ratingDto.getMovieId())
                .orElseThrow(() -> new ResourceNotFoundException("Фильм не найден с id: " + ratingDto.getMovieId()));

        Double newAverageRating = calculateAverageRating(ratingDto.getMovieId());
        movieService.updateRating(ratingDto.getMovieId(), newAverageRating);
    }

    @Override
    @Transactional(readOnly = true)
    public Double calculateAverageRating(Long movieId) {
        // SQL-запрос для получения среднего рейтинга из таблицы user_movies
        try {
            String sql = "SELECT COALESCE(AVG(rating), 0.0) FROM user_movies WHERE movie_id = ? AND rating IS NOT NULL";
            Double averageRating = jdbcTemplate.queryForObject(sql, Double.class, movieId);

            return getFinalRating(averageRating);
        } catch (Exception e) {
            return 0.0;
        }
    }

    private static Double getFinalRating(Double averageRating) {
        if (averageRating != null) {
            return Math.round(averageRating * 10.0) / 10.0;
        }

        return 0.0;
    }
}
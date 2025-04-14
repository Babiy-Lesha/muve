package main.movieservice.service;

import main.movieservice.dto.MovieCreateDto;
import main.movieservice.dto.MovieDto;

import java.util.List;

public interface MovieService {
    MovieDto createMovie(MovieCreateDto dto);
    MovieDto getMovieById(Long id);
    List<MovieDto> getAllMovies();
    List<MovieDto> searchMovies(String keyword);
    MovieDto updateMovie(Long id, MovieDto dto);
    MovieDto deleteMovie(Long id);
    boolean existsById(Long id);
    void updateRating(Long movieId, Double newRating);
}
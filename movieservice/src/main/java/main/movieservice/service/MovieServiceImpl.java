package main.movieservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import main.movieservice.dto.MovieCreateDto;
import main.movieservice.dto.MovieDeletedEventDto;
import main.movieservice.dto.MovieDto;
import main.movieservice.entity.Movie;
import main.movieservice.exception.ResourceNotFoundException;
import main.movieservice.kafka.MovieEventProducer;
import main.movieservice.mapper.MovieMapper;
import main.movieservice.repository.MovieRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;
    private final MovieEventProducer movieEventProducer;

    @Override
    @Transactional
    public MovieDto createMovie(MovieCreateDto dto) {
        if (movieRepository.existsByTitle(dto.getTitle())) {
            throw new IllegalArgumentException("Фильм уже существует");
        }

        Movie movie = movieMapper.toEntity(dto);
        Movie savedMovie = movieRepository.save(movie);
        return movieMapper.toDto(savedMovie);
    }

    @Override
    @Transactional(readOnly = true)
    public MovieDto getMovieById(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Фильм не найден с id: %d", id)));
        return movieMapper.toDto(movie);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieDto> getAllMovies() {
        List<Movie> movies = movieRepository.findAll();
        return movieMapper.toDtoList(movies);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieDto> searchMovies(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllMovies();
        }
        List<Movie> movies = movieRepository.searchByKeyword(keyword.trim());
        return movieMapper.toDtoList(movies);
    }

    @Override
    @Transactional
    public MovieDto updateMovie(Long id, MovieDto dto) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Фильм не найден с id: %d", id)));

        if (dto.getTitle() != null && !dto.getTitle().equals(movie.getTitle()) &&
                movieRepository.existsByTitle(dto.getTitle())) {
            throw new IllegalArgumentException("Фильм уже существует");
        }

        movieMapper.updateMovieFromDto(dto, movie);
        Movie updatedMovie = movieRepository.save(movie);
        return movieMapper.toDto(updatedMovie);
    }

    @Override
    @Transactional
    public MovieDto deleteMovie(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Фильм не найден с id: %d", id)));

        MovieDto movieDto = movieMapper.toDto(movie);
        movieRepository.deleteById(id);

        MovieDeletedEventDto event = MovieDeletedEventDto.builder()
                .movieId(id)
                .movieTitle(movie.getTitle())
                .build();

        movieEventProducer.sendMovieDeletedEvent(event);
        return movieDto;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return movieRepository.existsById(id);
    }

    @Override
    @Transactional
    public void updateRating(Long movieId, Double newRating) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Фильм не найден с id: %d", movieId)));

        movie.setRating(newRating);
        movieRepository.save(movie);
    }
}
package main.userservice.service;

import main.userservice.dto.UserMovieDto;

import java.util.List;

public interface UserMovieService {
    UserMovieDto addMovieToCollectionWithNote(Long userId, Long movieId, String note);
    List<UserMovieDto> getUserMovies(Long userId);
    UserMovieDto rateMovie(Long userId, Long movieId, Integer rating);
    void removeMovieFromCollection(Long userId, Long movieId);

    void removeMovieFromAllCollections(Long movieId);
}
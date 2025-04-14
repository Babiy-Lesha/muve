package main.movieservice.service;

import main.movieservice.dto.MovieRatingDto;

public interface MovieRatingService {
    void processRating(MovieRatingDto ratingDto);
    Double calculateAverageRating(Long movieId);
}
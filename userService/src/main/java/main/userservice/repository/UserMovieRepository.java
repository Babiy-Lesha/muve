package main.userservice.repository;

import main.userservice.entity.UserMovie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserMovieRepository extends JpaRepository<UserMovie, Long> {
    Optional<UserMovie> findByUserIdAndMovieId(Long userId, Long movieId);
    boolean existsByUserIdAndMovieId(Long userId, Long movieId);
    List<UserMovie> findByUserIdAndAddedToCollectionTrue(Long userId);
    List<UserMovie> findByMovieId(Long movieId);
}
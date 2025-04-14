package main.movieservice.repository;

import main.movieservice.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    boolean existsByTitle(String title);

    @Query("SELECT m FROM Movie m WHERE LOWER(m.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(m.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Movie> searchByKeyword(@Param("keyword") String keyword);
}
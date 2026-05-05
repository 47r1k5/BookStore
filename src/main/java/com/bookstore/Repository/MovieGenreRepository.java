package com.bookstore.Repository;

import com.bookstore.Entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieGenreRepository extends JpaRepository<MoviegenreEntity, Long> {
    List<MoviegenreEntity> findByMovie(MovieEntity movie);
    List<MoviegenreEntity> findByGenre(GenreEntity genre);
}
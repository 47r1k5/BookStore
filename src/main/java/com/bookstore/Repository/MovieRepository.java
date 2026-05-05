package com.bookstore.Repository;

import com.bookstore.Entity.MovieEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends JpaRepository<MovieEntity, Long> {
    MovieEntity findMovieEntityById(Integer id);

}
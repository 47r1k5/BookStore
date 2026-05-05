package com.bookstore.Repository;

import com.bookstore.Entity.GenreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface GenreRepository extends JpaRepository<GenreEntity, Long> {
    GenreEntity findById(Integer id);

    boolean existsByGenreNameIgnoreCase(String genreName);

    Optional<GenreEntity> findByGenreNameIgnoreCase(String genreName);

    List<GenreEntity> findByMainGenre(GenreEntity mainGenre);

    List<GenreEntity> findAllByIdIn(Set<Integer> updatedGenreIds);
}
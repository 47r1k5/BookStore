package com.bookstore.Repository;

import com.bookstore.Entity.GenreEntity;
import com.bookstore.Entity.MusicEntity;
import com.bookstore.Entity.MusicgenreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MusicGenreRepository extends JpaRepository<MusicgenreEntity, Long> {
    List<MusicgenreEntity> findByMusic(MusicEntity music);
    List<MusicgenreEntity> findByGenre(GenreEntity genre);
}
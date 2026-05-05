package com.bookstore.Repository;

import com.bookstore.Entity.MovieEntity;
import com.bookstore.Entity.MusicEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MusicRepository extends JpaRepository<MusicEntity, Long> {
    MusicEntity findMusicEntityById(Integer id);
}
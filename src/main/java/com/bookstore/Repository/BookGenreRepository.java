package com.bookstore.Repository;

import com.bookstore.Entity.BookEntity;
import com.bookstore.Entity.BookgenreEntity;
import com.bookstore.Entity.GenreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookGenreRepository extends JpaRepository<BookgenreEntity, Long> {
    Optional<BookgenreEntity> findBookgenreEntityById(Integer id);

    List<BookgenreEntity> findByBookIsbn(BookEntity bookIsbn);
    List<BookgenreEntity> findByGenre(GenreEntity genre);
}
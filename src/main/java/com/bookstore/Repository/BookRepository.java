package com.bookstore.Repository;

import com.bookstore.Entity.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, Long> {
    BookEntity findBookEntityByIsbn(String isbn);
}
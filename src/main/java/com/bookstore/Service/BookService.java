package com.bookstore.Service;

import com.bookstore.Entity.BookEntity;
import com.bookstore.Entity.BookgenreEntity;
import com.bookstore.Entity.GenreEntity;
import com.bookstore.Enum.CoverType;
import com.bookstore.POJOs.BookPOJO;
import com.bookstore.POJOs.GenrePOJO;
import com.bookstore.Repository.BookGenreRepository;
import com.bookstore.Repository.BookRepository;
import com.bookstore.Repository.GenreRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final GenreService genreService;
    private final BookGenreRepository bookGenreRepository;

    public BookService(
            BookRepository bookRepository,
            GenreService genreService,
            GenreRepository genreRepository,
            BookGenreRepository bookGenreRepository
    ) {
        this.bookRepository = bookRepository;
        this.genreService = genreService;
        this.bookGenreRepository = bookGenreRepository;
    }

    public List<BookPOJO> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(this::mapBookToPOJO)
                .toList();
    }

    public BookPOJO getBookByISBN(String isbn) {
        BookEntity book = bookRepository.findBookEntityByIsbn(isbn);
        return mapBookToPOJO(book);
    }

    public ResponseEntity<String> addBook(BookPOJO book) {
        try {
            BookEntity bookEntity = new BookEntity(
                    book.getIsbn(),
                    book.getTitle(),
                    book.getPrice(),
                    book.getEdition(),
                    CoverType.valueOf(book.getCover()),
                    book.getPageNum(),
                    book.getPublisher(),
                    book.getPhysicalSize(),
                    book.getAuthors(),
                    book.getStock()
            );

            bookRepository.save(bookEntity);

            for (GenreEntity genre : genreService.getManagedGenresFromPOJOs(book.getGenres())) {
                bookGenreRepository.save(new BookgenreEntity(bookEntity, genre));
            }

            return new ResponseEntity<>("Adding new book was successful", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Adding new book failed: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<String> updateBook(String isbn, BookPOJO updatedEntity) {
        try {
            BookEntity book = bookRepository.findBookEntityByIsbn(isbn);

            if (book == null) {
                return new ResponseEntity<>("Book not found with ISBN: " + isbn, HttpStatus.NOT_FOUND);
            }

            book.setTitle(updatedEntity.getTitle());
            book.setPrice(updatedEntity.getPrice());
            book.setEdition(updatedEntity.getEdition());
            book.setCover(CoverType.valueOf(updatedEntity.getCover()));
            book.setPageNum(updatedEntity.getPageNum());
            book.setPublisher(updatedEntity.getPublisher());
            book.setPhysicalSize(updatedEntity.getPhysicalSize());
            book.setAuthors(updatedEntity.getAuthors());
            book.setStock(updatedEntity.getStock());

            bookRepository.save(book);

            if (updatedEntity.getGenres() != null) {
                genreService.updateBookGenres(book, updatedEntity.getGenres());
            }

            return new ResponseEntity<>("Updating book was successful", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Updating book failed: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<String> deleteBook(String isbn) {
        try {
            BookEntity book = bookRepository.findBookEntityByIsbn(isbn);

            if (book == null) {
                return new ResponseEntity<>("Book not found with ISBN: " + isbn, HttpStatus.NOT_FOUND);
            }

            bookGenreRepository.deleteAll(bookGenreRepository.findByBookIsbn(book));
            bookRepository.delete(book);

            return new ResponseEntity<>("Deleting book was successful", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Deleting book failed: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    private BookPOJO mapBookToPOJO(BookEntity book) {
        if (book == null) {
            return null;
        }

        List<GenrePOJO> genres = genreService.mapGenresToPOJOs(
                genreService.getOneBooksGenres(book)
                        .stream()
                        .map(BookgenreEntity::getGenre)
                        .toList()
        );

        return new BookPOJO(
                book.getIsbn(),
                book.getTitle(),
                book.getPrice(),
                book.getEdition(),
                book.getCover() == null ? null : book.getCover().name(),
                book.getPageNum(),
                book.getPublisher(),
                book.getPhysicalSize(),
                book.getAuthors(),
                book.getStock(),
                genres
        );
    }
}

package com.bookstore.Service;

import com.bookstore.Entity.BookEntity;
import com.bookstore.Entity.BookgenreEntity;
import com.bookstore.Entity.GenreEntity;
import com.bookstore.Enum.CoverType;
import com.bookstore.POJOs.BookPOJO;
import com.bookstore.Repository.BookGenreRepository;
import com.bookstore.Repository.BookRepository;
import com.bookstore.Repository.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final GenreService genreService;
    private final BookGenreRepository bookGenreRepository;

    @Autowired
    public BookService(BookRepository bookRepository, GenreService genreService, GenreRepository genreRepository, BookGenreRepository bookGenreRepository){
        this.bookRepository = bookRepository;
        this.genreService = genreService;
        this.bookGenreRepository = bookGenreRepository;
    }

    public List<BookPOJO> getAllBooks(){
        List<BookEntity> bookEntities = bookRepository.findAll();
        List<BookPOJO> books = new ArrayList<>();
        for (BookEntity book:bookEntities){
            List<BookgenreEntity> bookgenreEntities=genreService.getOneBooksGenres(book);
            List<GenreEntity> genres=new ArrayList<>();
            for (BookgenreEntity bookgenre:bookgenreEntities){
                genres.add(bookgenre.getGenre());
            }
            books.add(new BookPOJO(book.getIsbn(),book.getTitle(),book.getPrice(),book.getEdition(),book.getCover().toString(),book.getPageNum(),book.getPublisher(),book.getPhysicalSize(),book.getAuthors(),book.getStock(),genres));
        }

        return books;
    }

    public BookPOJO getBookByISBN(String isbn) {
        BookEntity book = bookRepository.findBookEntityByIsbn(isbn);
        List<BookgenreEntity> bookgenreEntities=genreService.getOneBooksGenres(book);
        List<GenreEntity> genres=new ArrayList<>();
        for (BookgenreEntity bookgenre:bookgenreEntities){
            genres.add(bookgenre.getGenre());
        }
        return new BookPOJO(book.getIsbn(),book.getTitle(),book.getPrice(),book.getEdition(),book.getCover().toString(),book.getPageNum(),book.getPublisher(),book.getPhysicalSize(),book.getAuthors(),book.getStock(),genres);

    }

    public ResponseEntity<String> addBook(BookPOJO book){
        try{
            BookEntity bookEntity = new BookEntity(book.getIsbn(),book.getTitle(),book.getPrice(),book.getEdition(), CoverType.valueOf(book.getCover()),book.getPageNum(),book.getPublisher(),book.getPhysicalSize(),book.getAuthors(),book.getStock());

        bookRepository.save(bookEntity);

        for(var g: book.getGenres()){

            bookGenreRepository.save(new BookgenreEntity(bookEntity,g));
        }
        return new ResponseEntity<>("Adding new book was successful", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Adding new book failed: "+e, HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<String> updateBook(String isbn, BookPOJO updatedEntity) {
        try{
            BookEntity book = bookRepository.findBookEntityByIsbn(isbn);
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
            return new ResponseEntity<>("Updating book failed: " + e, HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<String> deleteBook(String isbn) {
        try{
            BookEntity book = bookRepository.findBookEntityByIsbn(isbn);
            bookRepository.delete(book);

            bookGenreRepository.deleteAll(bookGenreRepository.findByBookIsbn(book));

            return new ResponseEntity<>("Deleting book was successful", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Deleting book failed: " + e, HttpStatus.BAD_REQUEST);
        }
    }
}

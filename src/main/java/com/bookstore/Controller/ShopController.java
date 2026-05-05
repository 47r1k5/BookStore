package com.bookstore.Controller;

import com.bookstore.POJOs.BookPOJO;
import com.bookstore.POJOs.MoviePOJO;
import com.bookstore.POJOs.MusicPOJO;
import com.bookstore.Service.BookService;
import com.bookstore.Service.MovieService;
import com.bookstore.Service.MusicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookshop")
public class ShopController {
    private final BookService bookService;
    private final MovieService movieService;
    private final MusicService musicService;

    @Autowired
    public ShopController(BookService bookService, MovieService movieService, MusicService musicService) {
        this.bookService = bookService;
        this.movieService = movieService;
        this.musicService = musicService;
    }

    @GetMapping("/books")
    public List<BookPOJO> getAllBooks() {
        return bookService.getAllBooks();
    }

    @GetMapping("/movies")
    public List<MoviePOJO> getAllMovies() {
        return movieService.getAllMovies();
    }

    @GetMapping("/music")
    public List<MusicPOJO> getAllMusic() {
        return musicService.getAllMusic();
    }

    @GetMapping("/books/{isbn}")
    public BookPOJO getBookByISBN(@PathVariable String isbn) {
        return bookService.getBookByISBN(isbn);
    }

    @GetMapping("/movies/{id}")
    public MoviePOJO getMovieById(@PathVariable Integer id) {
        return movieService.getMovieById(id);
    }

    @GetMapping("/music/{id}")
    public MusicPOJO getMusicById(@PathVariable Integer id) {
        return musicService.getMusicById(id);
    }

}
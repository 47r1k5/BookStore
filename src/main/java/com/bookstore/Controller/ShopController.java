package com.bookstore.Controller;

import com.bookstore.POJOs.BookPOJO;
import com.bookstore.POJOs.GenrePOJO;
import com.bookstore.POJOs.MoviePOJO;
import com.bookstore.POJOs.MusicPOJO;
import com.bookstore.Service.BookService;
import com.bookstore.Service.GenreService;
import com.bookstore.Service.MovieService;
import com.bookstore.Service.MusicService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/bookshop")
public class ShopController {

    private final BookService bookService;
    private final MovieService movieService;
    private final MusicService musicService;
    private final GenreService genreService;

    public ShopController(
            BookService bookService,
            MovieService movieService,
            MusicService musicService,
            GenreService genreService
    ) {
        this.bookService = bookService;
        this.movieService = movieService;
        this.musicService = musicService;
        this.genreService = genreService;
    }

    @GetMapping("/books")
    public List<BookPOJO> getAllBooks() {
        return bookService.getAllBooks();
    }

    @GetMapping("/books/{isbn}")
    public BookPOJO getBookByISBN(@PathVariable String isbn) {
        return bookService.getBookByISBN(isbn);
    }

    @GetMapping("/movies")
    public List<MoviePOJO> getAllMovies() {
        return movieService.getAllMovies();
    }

    @GetMapping("/movies/{id}")
    public MoviePOJO getMovieById(@PathVariable Integer id) {
        return movieService.getMovieById(id);
    }

    @GetMapping("/music")
    public List<MusicPOJO> getAllMusic() {
        return musicService.getAllMusic();
    }

    @GetMapping("/music/{id}")
    public MusicPOJO getMusicById(@PathVariable Integer id) {
        return musicService.getMusicById(id);
    }

    @GetMapping("/genres")
    public List<GenrePOJO> getAllGenres() {
        return genreService.getAllGenres();
    }

    @GetMapping("/genres/{id}")
    public GenrePOJO getGenreById(@PathVariable Integer id) {
        return genreService.getGenreById(id);
    }
}

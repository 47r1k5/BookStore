package com.bookstore.Controller;

import com.bookstore.POJOs.BookPOJO;
import com.bookstore.POJOs.GenrePOJO;
import com.bookstore.POJOs.MoviePOJO;
import com.bookstore.POJOs.MusicPOJO;
import com.bookstore.Service.BookService;
import com.bookstore.Service.GenreService;
import com.bookstore.Service.MovieService;
import com.bookstore.Service.MusicService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bookshop")
public class StorageController {

    private final BookService bookService;
    private final MovieService movieService;
    private final MusicService musicService;
    private final GenreService genreService;

    public StorageController(
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

    @PostMapping("/books")
    public ResponseEntity<String> addBook(@RequestBody BookPOJO book) {
        return bookService.addBook(book);
    }

    @PutMapping("/books/{isbn}")
    public ResponseEntity<String> updateBook(
            @PathVariable String isbn,
            @RequestBody BookPOJO updatedBook
    ) {
        return bookService.updateBook(isbn, updatedBook);
    }

    @DeleteMapping("/books/{isbn}")
    public ResponseEntity<String> deleteBook(@PathVariable String isbn) {
        return bookService.deleteBook(isbn);
    }

    @PostMapping("/movies")
    public ResponseEntity<String> addMovie(@RequestBody MoviePOJO movie) {
        return movieService.addMovie(movie);
    }

    @PutMapping("/movies/{id}")
    public ResponseEntity<String> updateMovie(
            @PathVariable Long id,
            @RequestBody MoviePOJO updatedMovie
    ) {
        return movieService.updateMovie(id, updatedMovie);
    }

    @DeleteMapping("/movies/{id}")
    public ResponseEntity<String> deleteMovie(@PathVariable Long id) {
        return movieService.deleteMovie(id);
    }

    @PostMapping("/music")
    public ResponseEntity<String> addMusic(@RequestBody MusicPOJO music) {
        return musicService.addMusic(music);
    }

    @PutMapping("/music/{id}")
    public ResponseEntity<String> updateMusic(
            @PathVariable Long id,
            @RequestBody MusicPOJO updatedMusic
    ) {
        return musicService.updateMusic(id, updatedMusic);
    }

    @DeleteMapping("/music/{id}")
    public ResponseEntity<String> deleteMusic(@PathVariable Long id) {
        return musicService.deleteMusic(id);
    }

    @PostMapping("/genres")
    public ResponseEntity<String> addGenre(@RequestBody GenrePOJO genre) {
        return genreService.addGenre(genre);
    }

    @PutMapping("/genres/{id}")
    public ResponseEntity<String> updateGenre(
            @PathVariable Integer id,
            @RequestBody GenrePOJO genre
    ) {
        return genreService.updateGenre(id, genre);
    }

    @DeleteMapping("/genres/{id}")
    public ResponseEntity<String> deleteGenre(@PathVariable Integer id) {
        return genreService.deleteGenre(id);
    }
}

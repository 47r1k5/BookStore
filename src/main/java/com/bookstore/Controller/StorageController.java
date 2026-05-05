package com.bookstore.Controller;

import com.bookstore.Entity.GenreEntity;
import com.bookstore.POJOs.BookPOJO;
import com.bookstore.POJOs.MoviePOJO;
import com.bookstore.POJOs.MusicPOJO;
import com.bookstore.Service.BookService;
import com.bookstore.Service.GenreService;
import com.bookstore.Service.MovieService;
import com.bookstore.Service.MusicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bookshop")
public class StorageController {
    private final BookService bookService;
    private final MovieService movieService;
    private final MusicService musicService;
    private final GenreService genreService;

    @Autowired
    public StorageController(BookService bookService, MovieService movieService, MusicService musicService, GenreService genreService) {
        this.bookService = bookService;
        this.movieService = movieService;
        this.musicService = musicService;
        this.genreService = genreService;
    }

    @PostMapping("/books")
    public String addBook(@RequestBody BookPOJO book) {
        ResponseEntity<String> response = bookService.addBook(book);
        return response.getBody();
    }

    @PutMapping("/books/{isbn}")
    public String updateBook(@PathVariable String isbn,
                               @RequestBody BookPOJO updatedEntity) {
        ResponseEntity<String> response = bookService.updateBook(isbn, updatedEntity);
        return response.getBody();
    }

    @DeleteMapping("/books/{isbn}")
    public String deleteBook(@PathVariable String isbn) {
        ResponseEntity<String> response = bookService.deleteBook(isbn);
        return response.getBody();
    }


    @PostMapping("/music")
    public String addMusic(@RequestBody MusicPOJO music) {
        ResponseEntity<String> response = musicService.addMusic(music);
        return response.getBody();
    }

    @PutMapping("/music/{id}")
    public String updateMusic(@PathVariable Long id,
                                @RequestBody MusicPOJO updatedEntity) {
        ResponseEntity<String> response = musicService.updateMusic(id, updatedEntity);
        return response.getBody();
    }

    @DeleteMapping("/music/{id}")
    public String deleteMusic(@PathVariable Long id) {
        ResponseEntity<String> response = musicService.deleteMusic(id);
        return response.getBody();
    }

    @PostMapping("/movies")
    public String addMovie(@RequestBody MoviePOJO movie) {
        ResponseEntity<String> response = movieService.addMovie(movie);
        return response.getBody();

    }

    @PutMapping("/movies/{id}")
    public String updateMovie(@PathVariable Long id,
                                @RequestBody MoviePOJO updatedEntity) {
        ResponseEntity<String> response = movieService.updateMovie(id, updatedEntity);
        return response.getBody();
    }

    @DeleteMapping("/movies/{id}")
    public String deleteMovie(@PathVariable Long id) {
        ResponseEntity<String> response = movieService.deleteMovie(id);
        return response.getBody();
    }

    @PostMapping
    public ResponseEntity<String> addGenre(@RequestBody GenreEntity genre) {
        return genreService.addGenre(genre);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateGenre(
            @PathVariable Integer id,
            @RequestBody GenreEntity genre
    ) {
        return genreService.updateGenre(id, genre);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteGenre(@PathVariable Integer id) {
        return genreService.deleteGenre(id);
    }
}

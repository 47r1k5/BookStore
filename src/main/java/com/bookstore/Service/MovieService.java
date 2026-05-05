package com.bookstore.Service;

import com.bookstore.Entity.GenreEntity;
import com.bookstore.Entity.MovieEntity;
import com.bookstore.Entity.MoviegenreEntity;
import com.bookstore.POJOs.GenrePOJO;
import com.bookstore.POJOs.MoviePOJO;
import com.bookstore.Repository.GenreRepository;
import com.bookstore.Repository.MovieGenreRepository;
import com.bookstore.Repository.MovieRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final GenreService genreService;
    private final MovieGenreRepository movieGenreRepository;

    public MovieService(
            MovieRepository movieRepository,
            GenreService genreService,
            GenreRepository genreRepository,
            MovieGenreRepository movieGenreRepository
    ) {
        this.movieRepository = movieRepository;
        this.genreService = genreService;
        this.movieGenreRepository = movieGenreRepository;
    }

    public List<MoviePOJO> getAllMovies() {
        return movieRepository.findAll()
                .stream()
                .map(this::mapMovieToPOJO)
                .toList();
    }

    public MoviePOJO getMovieById(Integer id) {
        MovieEntity movie = movieRepository.findMovieEntityById(id);
        return mapMovieToPOJO(movie);
    }

    public ResponseEntity<String> addMovie(MoviePOJO movie) {
        try {
            MovieEntity movieEntity = new MovieEntity(
                    movie.getTitle(),
                    movie.getDirector(),
                    movie.getReleaseYear(),
                    movie.getPrice(),
                    movie.getLengthMin(),
                    movie.getStock()
            );

            movieRepository.save(movieEntity);

            for (GenreEntity genre : genreService.getManagedGenresFromPOJOs(movie.getGenres())) {
                movieGenreRepository.save(new MoviegenreEntity(movieEntity, genre));
            }

            return new ResponseEntity<>("Adding new movie was successful", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Adding new movie failed: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    public ResponseEntity<String> updateMovie(Long id, MoviePOJO updatedEntity) {
        try {
            MovieEntity movie = movieRepository.findById(id).orElse(null);

            if (movie == null) {
                return new ResponseEntity<>("Movie not found with ID: " + id, HttpStatus.NOT_FOUND);
            }

            movie.setTitle(updatedEntity.getTitle());
            movie.setDirector(updatedEntity.getDirector());
            movie.setReleaseYear(updatedEntity.getReleaseYear());
            movie.setPrice(updatedEntity.getPrice());
            movie.setLengthMin(updatedEntity.getLengthMin());
            movie.setStock(updatedEntity.getStock());

            movieRepository.save(movie);

            if (updatedEntity.getGenres() != null) {
                genreService.updateMovieGenres(movie, updatedEntity.getGenres());
            }

            return new ResponseEntity<>("Updating movie was successful", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Updating movie failed: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    public ResponseEntity<String> deleteMovie(Long id) {
        try {
            MovieEntity movie = movieRepository.findById(id).orElse(null);

            if (movie == null) {
                return new ResponseEntity<>("Movie not found with ID: " + id, HttpStatus.NOT_FOUND);
            }

            movieGenreRepository.deleteAll(movieGenreRepository.findByMovie(movie));
            movieRepository.delete(movie);

            return new ResponseEntity<>("Deleting movie was successful", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Deleting movie failed: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    private MoviePOJO mapMovieToPOJO(MovieEntity movie) {
        if (movie == null) {
            return null;
        }

        List<GenrePOJO> genres = genreService.mapGenresToPOJOs(
                genreService.getOneMovieGenres(movie)
                        .stream()
                        .map(MoviegenreEntity::getGenre)
                        .toList()
        );

        return new MoviePOJO(
                movie.getId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getReleaseYear(),
                movie.getPrice(),
                movie.getLengthMin(),
                movie.getStock(),
                genres
        );
    }
}

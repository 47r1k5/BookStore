package com.bookstore.Service;

import com.bookstore.Entity.*;
import com.bookstore.Repository.BookGenreRepository;
import com.bookstore.Repository.GenreRepository;
import com.bookstore.Repository.MovieGenreRepository;
import com.bookstore.Repository.MusicGenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GenreService {
    private final GenreRepository genreRepository;
    private final BookGenreRepository bookGenreRepository;
    private final MovieGenreRepository movieGenreRepository;
    private final MusicGenreRepository musicGenreRepository;

    @Autowired
    public GenreService(GenreRepository genreRepository, BookGenreRepository bookGenreRepository, MovieGenreRepository movieGenreRepository, MusicGenreRepository musicGenreRepository) {
        this.genreRepository = genreRepository;
        this.bookGenreRepository = bookGenreRepository;
        this.movieGenreRepository = movieGenreRepository;
        this.musicGenreRepository = musicGenreRepository;
    }

    public List<GenreEntity> getAllGenres(){
        return genreRepository.findAll();
    }

    public List<BookgenreEntity> getOneBooksGenres(BookEntity book){
        return bookGenreRepository.findByBookIsbn(book);
    }

    public GenreEntity getGenre(Integer id){
        return genreRepository.findById(id);
    }

    public List<MusicgenreEntity> getOneMusicsGenres(MusicEntity music) {
        return musicGenreRepository.findByMusic(music);
    }

    public List<MoviegenreEntity> getOneMovieGenres(MovieEntity movie) {
        return movieGenreRepository.findByMovie(movie);
    }

    public void updateBookGenres(BookEntity book, List<GenreEntity> updatedGenres) {
        Set<Integer> updatedGenreIds = updatedGenres.stream()
                .filter(Objects::nonNull)
                .map(GenreEntity::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<BookgenreEntity> existingBookGenres =
                bookGenreRepository.findByBookIsbn(book);

        Set<Integer> existingGenreIds = existingBookGenres.stream()
                .map(bookGenre -> bookGenre.getGenre().getId())
                .collect(Collectors.toSet());

        if (existingGenreIds.equals(updatedGenreIds)) {
            return;
        }

        List<GenreEntity> managedGenres = genreRepository.findAllByIdIn(updatedGenreIds);

        if (managedGenres.size() != updatedGenreIds.size()) {
            throw new IllegalArgumentException("One or more genres do not exist");
        }

        bookGenreRepository.deleteAll(existingBookGenres);

        List<BookgenreEntity> newBookGenres = managedGenres.stream()
                .map(genre -> new BookgenreEntity(book, genre))
                .toList();

        bookGenreRepository.saveAll(newBookGenres);
    }

    public void updateMovieGenres(MovieEntity movie, List<GenreEntity> updatedGenres) {
        Set<Integer> updatedGenreIds = updatedGenres.stream()
                .filter(Objects::nonNull)
                .map(GenreEntity::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<MoviegenreEntity> existingMovieGenres =
                movieGenreRepository.findByMovie(movie);

        Set<Integer> existingGenreIds = existingMovieGenres.stream()
                .map(movieGenre -> movieGenre.getGenre().getId())
                .collect(Collectors.toSet());

        if (existingGenreIds.equals(updatedGenreIds)) {
            return;
        }

        List<GenreEntity> managedGenres = genreRepository.findAllByIdIn(updatedGenreIds);

        if (managedGenres.size() != updatedGenreIds.size()) {
            throw new IllegalArgumentException("One or more movie genres do not exist");
        }

        movieGenreRepository.deleteAll(existingMovieGenres);

        List<MoviegenreEntity> newMovieGenres = managedGenres.stream()
                .map(genre -> new MoviegenreEntity(movie, genre))
                .toList();

        movieGenreRepository.saveAll(newMovieGenres);
    }

    public void updateMusicGenres(MusicEntity music, List<GenreEntity> updatedGenres) {
        Set<Integer> updatedGenreIds = updatedGenres.stream()
                .filter(Objects::nonNull)
                .map(GenreEntity::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<MusicgenreEntity> existingMusicGenres =
                musicGenreRepository.findByMusic(music);

        Set<Integer> existingGenreIds = existingMusicGenres.stream()
                .map(musicGenre -> musicGenre.getGenre().getId())
                .collect(Collectors.toSet());

        if (existingGenreIds.equals(updatedGenreIds)) {
            return;
        }

        List<GenreEntity> managedGenres = genreRepository.findAllByIdIn(updatedGenreIds);

        if (managedGenres.size() != updatedGenreIds.size()) {
            throw new IllegalArgumentException("One or more music genres do not exist");
        }

        musicGenreRepository.deleteAll(existingMusicGenres);

        List<MusicgenreEntity> newMusicGenres = managedGenres.stream()
                .map(genre -> new MusicgenreEntity(music, genre))
                .toList();

        musicGenreRepository.saveAll(newMusicGenres);
    }

    @Transactional
    public ResponseEntity<String> addGenre(GenreEntity genre) {
        try {
            if (genre.getGenreName() == null || genre.getGenreName().isBlank()) {
                return new ResponseEntity<>("Genre name cannot be empty", HttpStatus.BAD_REQUEST);
            }

            if (genre.getGenreName().length() > 20) {
                return new ResponseEntity<>("Genre name cannot be longer than 20 characters", HttpStatus.BAD_REQUEST);
            }

            if (genreRepository.existsByGenreNameIgnoreCase(genre.getGenreName())) {
                return new ResponseEntity<>("Genre already exists: " + genre.getGenreName(), HttpStatus.CONFLICT);
            }

            GenreEntity newGenre = new GenreEntity();
            newGenre.setGenreName(genre.getGenreName());

            if (genre.getMainGenre() != null && genre.getMainGenre().getId() != null) {
                GenreEntity mainGenre = genreRepository.findById(genre.getMainGenre().getId());

                newGenre.setMainGenre(mainGenre);
            } else {
                newGenre.setMainGenre(null);
            }

            genreRepository.save(newGenre);

            return new ResponseEntity<>("Adding new genre was successful", HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>("Adding new genre failed: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    public ResponseEntity<String> updateGenre(Integer id, GenreEntity updatedGenre) {
        try {
            GenreEntity genre = genreRepository.findById(id);

            if (genre == null) {
                return new ResponseEntity<>("Genre not found with ID: " + id, HttpStatus.NOT_FOUND);
            }

            if (updatedGenre.getGenreName() == null || updatedGenre.getGenreName().isBlank()) {
                return new ResponseEntity<>("Genre name cannot be empty", HttpStatus.BAD_REQUEST);
            }

            if (updatedGenre.getGenreName().length() > 20) {
                return new ResponseEntity<>("Genre name cannot be longer than 20 characters", HttpStatus.BAD_REQUEST);
            }

            Optional<GenreEntity> genreWithSameName =
                    genreRepository.findByGenreNameIgnoreCase(updatedGenre.getGenreName());

            if (genreWithSameName.isPresent() && !genreWithSameName.get().getId().equals(id)) {
                return new ResponseEntity<>("Another genre already exists with this name: " + updatedGenre.getGenreName(), HttpStatus.CONFLICT);
            }

            genre.setGenreName(updatedGenre.getGenreName());

            if (updatedGenre.getMainGenre() != null && updatedGenre.getMainGenre().getId() != null) {
                Integer mainGenreId = updatedGenre.getMainGenre().getId();

                if (mainGenreId.equals(id)) {
                    return new ResponseEntity<>("A genre cannot be its own main genre", HttpStatus.BAD_REQUEST);
                }

                GenreEntity mainGenre = genreRepository.findById(mainGenreId);

                genre.setMainGenre(mainGenre);
            } else {
                genre.setMainGenre(null);
            }

            genreRepository.save(genre);

            return new ResponseEntity<>("Updating genre was successful", HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>("Updating genre failed: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    public ResponseEntity<String> deleteGenre(Integer id) {
        try {
            GenreEntity genre = genreRepository.findById(id);

            if (genre == null) {
                return new ResponseEntity<>("Genre not found with ID: " + id, HttpStatus.NOT_FOUND);
            }

            bookGenreRepository.deleteAll(bookGenreRepository.findByGenre(genre));
            movieGenreRepository.deleteAll(movieGenreRepository.findByGenre(genre));
            musicGenreRepository.deleteAll(musicGenreRepository.findByGenre(genre));

            List<GenreEntity> subGenres = genreRepository.findByMainGenre(genre);

            for (GenreEntity subGenre : subGenres) {
                subGenre.setMainGenre(null);
            }

            genreRepository.saveAll(subGenres);

            genreRepository.delete(genre);

            return new ResponseEntity<>("Deleting genre was successful", HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>("Deleting genre failed: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}

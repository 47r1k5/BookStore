package com.bookstore.Service;

import com.bookstore.Entity.BookEntity;
import com.bookstore.Entity.BookgenreEntity;
import com.bookstore.Entity.GenreEntity;
import com.bookstore.Entity.MovieEntity;
import com.bookstore.Entity.MoviegenreEntity;
import com.bookstore.Entity.MusicEntity;
import com.bookstore.Entity.MusicgenreEntity;
import com.bookstore.POJOs.GenrePOJO;
import com.bookstore.Repository.BookGenreRepository;
import com.bookstore.Repository.GenreRepository;
import com.bookstore.Repository.MovieGenreRepository;
import com.bookstore.Repository.MusicGenreRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
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

    public GenreService(
            GenreRepository genreRepository,
            BookGenreRepository bookGenreRepository,
            MovieGenreRepository movieGenreRepository,
            MusicGenreRepository musicGenreRepository
    ) {
        this.genreRepository = genreRepository;
        this.bookGenreRepository = bookGenreRepository;
        this.movieGenreRepository = movieGenreRepository;
        this.musicGenreRepository = musicGenreRepository;
    }

    public List<GenrePOJO> getAllGenres() {
        return genreRepository.findAll()
                .stream()
                .map(this::mapGenreToPOJO)
                .toList();
    }

    public GenrePOJO getGenreById(Integer id) {
        return mapGenreToPOJO(genreRepository.findById(id));
    }

    public GenreEntity getGenre(Integer id) {
        return genreRepository.findById(id);
    }

    public GenrePOJO mapGenreToPOJO(GenreEntity genre) {
        if (genre == null) {
            return null;
        }

        Integer mainGenreId = genre.getMainGenre() == null
                ? null
                : genre.getMainGenre().getId();

        return new GenrePOJO(
                genre.getId(),
                genre.getGenreName(),
                mainGenreId
        );
    }

    public List<GenrePOJO> mapGenresToPOJOs(List<GenreEntity> genres) {
        if (genres == null) {
            return Collections.emptyList();
        }

        return genres.stream()
                .map(this::mapGenreToPOJO)
                .filter(Objects::nonNull)
                .toList();
    }

    public List<GenreEntity> getManagedGenresFromPOJOs(List<GenrePOJO> genres) {
        if (genres == null || genres.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Integer> genreIds = genres.stream()
                .filter(Objects::nonNull)
                .map(GenrePOJO::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (genreIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<GenreEntity> managedGenres = genreRepository.findAllByIdIn(genreIds);

        if (managedGenres.size() != genreIds.size()) {
            throw new IllegalArgumentException("One or more genres do not exist");
        }

        return managedGenres;
    }

    public List<BookgenreEntity> getOneBooksGenres(BookEntity book) {
        return bookGenreRepository.findByBookIsbn(book);
    }

    public List<MusicgenreEntity> getOneMusicsGenres(MusicEntity music) {
        return musicGenreRepository.findByMusic(music);
    }

    public List<MoviegenreEntity> getOneMovieGenres(MovieEntity movie) {
        return movieGenreRepository.findByMovie(movie);
    }

    public void updateBookGenres(BookEntity book, List<GenrePOJO> updatedGenres) {
        Set<Integer> updatedGenreIds = getGenreIds(updatedGenres);

        List<BookgenreEntity> existingBookGenres =
                bookGenreRepository.findByBookIsbn(book);

        Set<Integer> existingGenreIds = existingBookGenres.stream()
                .map(bookGenre -> bookGenre.getGenre().getId())
                .collect(Collectors.toSet());

        if (existingGenreIds.equals(updatedGenreIds)) {
            return;
        }

        List<GenreEntity> managedGenres = getManagedGenresFromPOJOs(updatedGenres);

        bookGenreRepository.deleteAll(existingBookGenres);

        List<BookgenreEntity> newBookGenres = managedGenres.stream()
                .map(genre -> new BookgenreEntity(book, genre))
                .toList();

        bookGenreRepository.saveAll(newBookGenres);
    }

    public void updateMovieGenres(MovieEntity movie, List<GenrePOJO> updatedGenres) {
        Set<Integer> updatedGenreIds = getGenreIds(updatedGenres);

        List<MoviegenreEntity> existingMovieGenres =
                movieGenreRepository.findByMovie(movie);

        Set<Integer> existingGenreIds = existingMovieGenres.stream()
                .map(movieGenre -> movieGenre.getGenre().getId())
                .collect(Collectors.toSet());

        if (existingGenreIds.equals(updatedGenreIds)) {
            return;
        }

        List<GenreEntity> managedGenres = getManagedGenresFromPOJOs(updatedGenres);

        movieGenreRepository.deleteAll(existingMovieGenres);

        List<MoviegenreEntity> newMovieGenres = managedGenres.stream()
                .map(genre -> new MoviegenreEntity(movie, genre))
                .toList();

        movieGenreRepository.saveAll(newMovieGenres);
    }

    public void updateMusicGenres(MusicEntity music, List<GenrePOJO> updatedGenres) {
        Set<Integer> updatedGenreIds = getGenreIds(updatedGenres);

        List<MusicgenreEntity> existingMusicGenres =
                musicGenreRepository.findByMusic(music);

        Set<Integer> existingGenreIds = existingMusicGenres.stream()
                .map(musicGenre -> musicGenre.getGenre().getId())
                .collect(Collectors.toSet());

        if (existingGenreIds.equals(updatedGenreIds)) {
            return;
        }

        List<GenreEntity> managedGenres = getManagedGenresFromPOJOs(updatedGenres);

        musicGenreRepository.deleteAll(existingMusicGenres);

        List<MusicgenreEntity> newMusicGenres = managedGenres.stream()
                .map(genre -> new MusicgenreEntity(music, genre))
                .toList();

        musicGenreRepository.saveAll(newMusicGenres);
    }

    @Transactional
    public ResponseEntity<String> addGenre(GenrePOJO genre) {
        try {
            ResponseEntity<String> validationResponse = validateGenre(genre);

            if (validationResponse != null) {
                return validationResponse;
            }

            if (genreRepository.existsByGenreNameIgnoreCase(genre.getGenreName())) {
                return new ResponseEntity<>("Genre already exists: " + genre.getGenreName(), HttpStatus.CONFLICT);
            }

            GenreEntity newGenre = new GenreEntity();
            newGenre.setGenreName(genre.getGenreName().trim());

            if (genre.getMainGenreId() != null) {
                GenreEntity mainGenre = genreRepository.findById(genre.getMainGenreId());

                if (mainGenre == null) {
                    return new ResponseEntity<>("Main genre not found with ID: " + genre.getMainGenreId(), HttpStatus.NOT_FOUND);
                }

                newGenre.setMainGenre(mainGenre);
            }

            genreRepository.save(newGenre);

            return new ResponseEntity<>("Adding new genre was successful", HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>("Adding new genre failed: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    public ResponseEntity<String> updateGenre(Integer id, GenrePOJO updatedGenre) {
        try {
            GenreEntity genre = genreRepository.findById(id);

            if (genre == null) {
                return new ResponseEntity<>("Genre not found with ID: " + id, HttpStatus.NOT_FOUND);
            }

            ResponseEntity<String> validationResponse = validateGenre(updatedGenre);

            if (validationResponse != null) {
                return validationResponse;
            }

            Optional<GenreEntity> genreWithSameName =
                    genreRepository.findByGenreNameIgnoreCase(updatedGenre.getGenreName());

            if (genreWithSameName.isPresent() && !genreWithSameName.get().getId().equals(id)) {
                return new ResponseEntity<>("Another genre already exists with this name: " + updatedGenre.getGenreName(), HttpStatus.CONFLICT);
            }

            genre.setGenreName(updatedGenre.getGenreName().trim());

            if (updatedGenre.getMainGenreId() != null) {
                Integer mainGenreId = updatedGenre.getMainGenreId();

                if (mainGenreId.equals(id)) {
                    return new ResponseEntity<>("A genre cannot be its own main genre", HttpStatus.BAD_REQUEST);
                }

                GenreEntity mainGenre = genreRepository.findById(mainGenreId);

                if (mainGenre == null) {
                    return new ResponseEntity<>("Main genre not found with ID: " + mainGenreId, HttpStatus.NOT_FOUND);
                }

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

    private Set<Integer> getGenreIds(List<GenrePOJO> genres) {
        if (genres == null) {
            return Collections.emptySet();
        }

        return genres.stream()
                .filter(Objects::nonNull)
                .map(GenrePOJO::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private ResponseEntity<String> validateGenre(GenrePOJO genre) {
        if (genre == null) {
            return new ResponseEntity<>("Genre cannot be null", HttpStatus.BAD_REQUEST);
        }

        if (genre.getGenreName() == null || genre.getGenreName().isBlank()) {
            return new ResponseEntity<>("Genre name cannot be empty", HttpStatus.BAD_REQUEST);
        }

        if (genre.getGenreName().length() > 20) {
            return new ResponseEntity<>("Genre name cannot be longer than 20 characters", HttpStatus.BAD_REQUEST);
        }

        return null;
    }
}

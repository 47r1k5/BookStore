package com.bookstore.Service;

import com.bookstore.Entity.GenreEntity;
import com.bookstore.Entity.MusicEntity;
import com.bookstore.Entity.MusicgenreEntity;
import com.bookstore.POJOs.GenrePOJO;
import com.bookstore.POJOs.MusicPOJO;
import com.bookstore.Repository.GenreRepository;
import com.bookstore.Repository.MusicGenreRepository;
import com.bookstore.Repository.MusicRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MusicService {

    private final MusicRepository musicRepository;
    private final GenreService genreService;
    private final MusicGenreRepository musicGenreRepository;

    public MusicService(
            MusicRepository musicRepository,
            GenreRepository genreRepository,
            GenreService genreService,
            MusicGenreRepository musicGenreRepository
    ) {
        this.musicRepository = musicRepository;
        this.genreService = genreService;
        this.musicGenreRepository = musicGenreRepository;
    }

    public List<MusicPOJO> getAllMusic() {
        return musicRepository.findAll()
                .stream()
                .map(this::mapMusicToPOJO)
                .toList();
    }

    public MusicPOJO getMusicById(Integer id) {
        MusicEntity music = musicRepository.findMusicEntityById(id);
        return mapMusicToPOJO(music);
    }

    public ResponseEntity<String> addMusic(MusicPOJO music) {
        try {
            MusicEntity musicEntity = new MusicEntity(
                    music.getTitle(),
                    music.getReleaseYear(),
                    music.getPrice(),
                    music.getStock(),
                    music.getArtist()
            );

            musicRepository.save(musicEntity);

            for (GenreEntity genre : genreService.getManagedGenresFromPOJOs(music.getGenres())) {
                musicGenreRepository.save(new MusicgenreEntity(musicEntity, genre));
            }

            return new ResponseEntity<>("Adding new music was successful", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Adding new music failed: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    public ResponseEntity<String> updateMusic(Long id, MusicPOJO updatedEntity) {
        try {
            MusicEntity music = musicRepository.findById(id).orElse(null);

            if (music == null) {
                return new ResponseEntity<>("Music not found with ID: " + id, HttpStatus.NOT_FOUND);
            }

            music.setTitle(updatedEntity.getTitle());
            music.setReleaseYear(updatedEntity.getReleaseYear());
            music.setPrice(updatedEntity.getPrice());
            music.setStock(updatedEntity.getStock());
            music.setArtist(updatedEntity.getArtist());

            musicRepository.save(music);

            if (updatedEntity.getGenres() != null) {
                genreService.updateMusicGenres(music, updatedEntity.getGenres());
            }

            return new ResponseEntity<>("Updating music was successful", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Updating music failed: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    public ResponseEntity<String> deleteMusic(Long id) {
        try {
            MusicEntity music = musicRepository.findById(id).orElse(null);

            if (music == null) {
                return new ResponseEntity<>("Music not found with ID: " + id, HttpStatus.NOT_FOUND);
            }

            musicGenreRepository.deleteAll(musicGenreRepository.findByMusic(music));
            musicRepository.delete(music);

            return new ResponseEntity<>("Deleting music was successful", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Deleting music failed: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    private MusicPOJO mapMusicToPOJO(MusicEntity music) {
        if (music == null) {
            return null;
        }

        List<GenrePOJO> genres = genreService.mapGenresToPOJOs(
                genreService.getOneMusicsGenres(music)
                        .stream()
                        .map(MusicgenreEntity::getGenre)
                        .toList()
        );

        return new MusicPOJO(
                music.getId(),
                music.getTitle(),
                music.getReleaseYear(),
                music.getPrice(),
                music.getStock(),
                music.getArtist(),
                genres
        );
    }
}

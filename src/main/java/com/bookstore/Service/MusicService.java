package com.bookstore.Service;

import com.bookstore.Entity.*;
import com.bookstore.POJOs.BookPOJO;
import com.bookstore.POJOs.MoviePOJO;
import com.bookstore.POJOs.MusicPOJO;
import com.bookstore.Repository.BookGenreRepository;
import com.bookstore.Repository.GenreRepository;
import com.bookstore.Repository.MusicGenreRepository;
import com.bookstore.Repository.MusicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class MusicService {
    private final MusicRepository musicRepository;
    private final GenreService genreService;
    private final MusicGenreRepository musicGenreRepository;

    @Autowired
    public MusicService(MusicRepository musicRepository, GenreRepository genreRepository, GenreService genreService, MusicGenreRepository musicGenreRepository) {
        this.musicRepository = musicRepository;
        this.genreService = genreService;
        this.musicGenreRepository = musicGenreRepository;
    }

    public List<MusicPOJO> getAllMusic(){
        List<MusicEntity> musicEntities = musicRepository.findAll();
        List<MusicPOJO> musicList = new ArrayList<>();
        for (MusicEntity music:musicEntities){
            List<MusicgenreEntity> musicgenreEntities=genreService.getOneMusicsGenres(music);
            List<GenreEntity> genres=new ArrayList<>();
            for (MusicgenreEntity musicgenreEntity:musicgenreEntities){
                genres.add(musicgenreEntity.getGenre());
            }
            musicList.add(new MusicPOJO(music.getId(),music.getTitle(),music.getReleaseYear(),music.getPrice(),music.getStock(),music.getArtist(),genres));
        }

        return musicList;
    }

    public MusicPOJO getMusicById(Integer id){
        MusicEntity music = musicRepository.findMusicEntityById(id);
        List<MusicgenreEntity> musicgenreEntities=genreService.getOneMusicsGenres(music);
        List<GenreEntity> genres=new ArrayList<>();
        for (MusicgenreEntity musicgenreEntity:musicgenreEntities){
            genres.add(musicgenreEntity.getGenre());
        }
        return new MusicPOJO(music.getId(),music.getTitle(),music.getReleaseYear(),music.getPrice(),music.getStock(),music.getArtist(),genres);
    }

    public ResponseEntity<String> addMusic(MusicPOJO music){
        try{
            MusicEntity musicEntity = new MusicEntity(music.getTitle(), music.getReleaseYear(), music.getPrice(),music.getStock(),music.getArtist());

            musicRepository.save(musicEntity);

            for(var g: music.getGenres()){

                musicGenreRepository.save(new MusicgenreEntity(musicEntity,g));
            }
            return new ResponseEntity<>("Adding new music was successful", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Adding new music failed: "+e, HttpStatus.BAD_REQUEST);
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
}

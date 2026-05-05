package com.bookstore.POJOs;

import com.bookstore.Entity.GenreEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MusicPOJO {
    Integer id;
    String title;
    Short releaseYear;
    Integer price;
    Short stock;
    List<String> artist;
    List<GenreEntity> genres;
}

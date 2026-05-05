package com.bookstore.POJOs;

import com.bookstore.CompositeType.PersonName;
import com.bookstore.Entity.GenreEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MoviePOJO {
    Integer id;
    String title;
    List<PersonName> director;
    Short releaseYear;
    Integer price;
    Short lengthMin;
    Short stock;
    List<GenreEntity> genres;
}

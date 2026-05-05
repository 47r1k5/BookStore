package com.bookstore.POJOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MusicPOJO {
    private Integer id;
    private String title;
    private Short releaseYear;
    private Integer price;
    private Short stock;
    private List<String> artist;
    private List<GenrePOJO> genres;
}

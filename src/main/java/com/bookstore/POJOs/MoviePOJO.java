package com.bookstore.POJOs;

import com.bookstore.CompositeType.PersonName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MoviePOJO {
    private Integer id;
    private String title;
    private List<PersonName> director;
    private Short releaseYear;
    private Integer price;
    private Short lengthMin;
    private Short stock;
    private List<GenrePOJO> genres;
}

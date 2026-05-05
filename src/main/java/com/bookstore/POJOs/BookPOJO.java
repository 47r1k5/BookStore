package com.bookstore.POJOs;

import com.bookstore.CompositeType.PersonName;
import com.bookstore.CompositeType.Size2D;
import com.bookstore.Entity.GenreEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BookPOJO {
    String isbn;
    String title;
    Integer price;
    String edition;
    String cover;
    Integer pageNum;
    String publisher;
    Size2D physicalSize;
    List<PersonName> authors;
    Short stock;
    List<GenreEntity> genres;
}

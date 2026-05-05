package com.bookstore.POJOs;

import com.bookstore.CompositeType.PersonName;
import com.bookstore.CompositeType.Size2D;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookPOJO {
    private String isbn;
    private String title;
    private Integer price;
    private String edition;
    private String cover;
    private Integer pageNum;
    private String publisher;
    private Size2D physicalSize;
    private List<PersonName> authors;
    private Short stock;
    private List<GenrePOJO> genres;
}

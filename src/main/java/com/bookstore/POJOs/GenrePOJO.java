package com.bookstore.POJOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenrePOJO {
    private Integer id;
    private String genreName;
    private Integer mainGenreId;
}

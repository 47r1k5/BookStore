package com.bookstore.POJOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartMovieItemPOJO {
    private Short quantity;
    private MoviePOJO movie;
}
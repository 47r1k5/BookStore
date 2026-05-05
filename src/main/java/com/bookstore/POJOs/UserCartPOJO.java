package com.bookstore.POJOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCartPOJO {
    private List<CartBookItemPOJO> books;
    private List<CartMovieItemPOJO> movies;
    private List<CartMusicItemPOJO> music;
}
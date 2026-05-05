package com.bookstore.POJOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartMusicItemPOJO {
    private Short quantity;
    private MusicPOJO music;
}
package com.bookstore.POJOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartBookItemPOJO {
    private Short quantity;
    private BookPOJO book;
}
package com.bookstore.POJOs;

import com.bookstore.Enum.ProductType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemPOJO {
    private String productId;
    private ProductType prodType;
    private Short quantity;
}
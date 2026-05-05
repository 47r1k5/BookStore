package com.bookstore.CompositeType;

import com.bookstore.Enum.ProductType;

import java.io.Serializable;

public record PurchaseCartItem(
        Integer cartId,
        String productId,
        ProductType prodType,
        Short quantity
) implements Serializable {}
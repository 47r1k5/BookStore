package com.bookstore.CompositeType;

import java.io.Serializable;

public record Address(
        String street,
        String city,
        String num,
        String postalCode,
        String country
) implements Serializable {}

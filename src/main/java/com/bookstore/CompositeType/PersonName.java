package com.bookstore.CompositeType;

import java.io.Serializable;

public record PersonName(
        String firstName,
        String lastName
) implements Serializable {}
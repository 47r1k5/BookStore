package com.bookstore.CompositeType;

import java.io.Serializable;

public record Size2D(
        Short x,
        Short y
) implements Serializable {}

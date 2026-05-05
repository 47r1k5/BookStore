package com.bookstore.CompositeType;

import jakarta.persistence.Embeddable;
import org.hibernate.annotations.Struct;

@Embeddable
@Struct(name = "size_2d_type", attributes = {"x", "y"})
public record Size2D(
        Short x,
        Short y
) {}

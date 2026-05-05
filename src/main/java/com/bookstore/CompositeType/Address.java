package com.bookstore.CompositeType;

import jakarta.persistence.Embeddable;
import org.hibernate.annotations.Struct;

@Embeddable
@Struct(
        name = "address_type",
        attributes = {
                "street",
                "city",
                "num",
                "postal_code",
                "country"
        }
)
public record Address(
        String street,
        String city,
        String num,
        String postalCode,
        String country
) {}
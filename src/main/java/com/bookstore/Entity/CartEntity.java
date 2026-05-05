package com.bookstore.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "cart")
public class CartEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -6030974065662115127L;

    @EmbeddedId
    private CartId id;

    @MapsId("cartId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", referencedColumnName = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "quantity")
    private Short quantity;
}
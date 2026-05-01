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
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @SequenceGenerator(name = "cart_id_gen", sequenceName = "genre_genre_id_seq", allocationSize = 1)
    @JoinColumn(name = "cart_id")
    private BsUser cart;

    @Column(name = "product_id", length = Integer.MAX_VALUE)
    private String productId;

    @Column(name = "prod_type", columnDefinition = "product_type")
    private Object prodType;

    @Column(name = "quantity")
    private Short quantity;


}
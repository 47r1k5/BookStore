package com.bookstore.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "purchase")
public class PurchaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = -9023473485231708775L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "purchase_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "payment_option", columnDefinition = "pay_option_type not null")
    private Object paymentOption;

    @Column(name = "address", columnDefinition = "address_type not null")
    private Object address;

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    @Column(name = "cart", columnDefinition = "cart[]")
    private Object cart;


}
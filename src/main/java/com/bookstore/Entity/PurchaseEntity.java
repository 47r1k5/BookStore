package com.bookstore.Entity;

import com.bookstore.CompositeType.Address;
import com.bookstore.CompositeType.AddressUserType;
import com.bookstore.CompositeType.CartArrayUserType;
import com.bookstore.CompositeType.PurchaseCartItem;
import com.bookstore.Enum.PayOptionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "payment_option", columnDefinition = "pay_option_type not null", nullable = false)
    private PayOptionType paymentOption;

    @Type(AddressUserType.class)
    @Column(name = "address", nullable = false, columnDefinition = "address_type")
    private Address address;

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    @Type(value = CartArrayUserType.class)
    @Column(name = "cart", columnDefinition = "cart[]")
    private List<PurchaseCartItem> cart = new ArrayList<>();
}
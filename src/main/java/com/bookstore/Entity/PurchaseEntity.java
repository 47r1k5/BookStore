/*package com.bookstore.Entity;

import com.bookstore.CompositeType.Address;
import com.bookstore.Enum.PayOptionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "payment_option", columnDefinition = "pay_option_type not null", nullable = false)
    private PayOptionType paymentOption;

    @Column(name = "address", columnDefinition = "address_type not null")
    private Address address;

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

}*/
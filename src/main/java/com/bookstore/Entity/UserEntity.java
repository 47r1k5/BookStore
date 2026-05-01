package com.bookstore.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "bs_user", uniqueConstraints = {
        @UniqueConstraint(name = "bs_user_username_key",
                columnNames = {"username"}),
        @UniqueConstraint(name = "bs_user_email_key",
                columnNames = {"email"})})
public class UserEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 5162134906341027651L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bs_user_id_gen")
    @SequenceGenerator(name = "bs_user_id_gen", sequenceName = "bs_user_user_id_seq", allocationSize = 1)
    @Column(name = "user_id", nullable = false)
    private Integer id;

    @Column(name = "username", nullable = false, length = 10)
    private String username;

    @Column(name = "email", nullable = false, length = 250)
    private String email;

    @Column(name = "pass", nullable = false, length = 20)
    private String pass;

    @Column(name = "fullname", columnDefinition = "name_type")
    private Object fullname;

    @ColumnDefault("'user'")
    @Column(name = "permissions", columnDefinition = "perm_type")
    private Object permissions;

    @ColumnDefault("false")
    @Column(name = "regular")
    private Boolean regular;

    @OneToMany
    @JoinColumn(name = "cart_id")
    private Set<CartEntity> carts = new LinkedHashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<PurchaseEntity> purchases = new LinkedHashSet<>();


}
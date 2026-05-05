package com.bookstore.Entity;

import com.bookstore.CompositeType.NameTypeArrayUserType;
import com.bookstore.CompositeType.NameTypeUserType;
import com.bookstore.CompositeType.PersonName;
import com.bookstore.Enum.PermissionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

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

    @Column(name = "pass", nullable = false)
    private String pass;

    @Type(value = NameTypeUserType.class)
    @Column(name = "fullname", columnDefinition = "name_type")
    private PersonName fullname;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @ColumnDefault("'USER'")
    @Column(name = "permissions", columnDefinition = "perm_type", nullable = false)
    private PermissionType permissions;

    @ColumnDefault("false")
    @Column(name = "regular")
    private Boolean regular;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CartEntity> carts = new LinkedHashSet<>();
}
package com.bookstore.Entity;

import com.bookstore.CompositeType.NameTypeArrayUserType;
import com.bookstore.CompositeType.PersonName;
import com.bookstore.CompositeType.Size2D;
import com.bookstore.CompositeType.Size2DUserType;
import com.bookstore.Enum.CoverType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "book")
public class BookEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 6165673022371847468L;
    @Id
    @Column(name = "isbn", nullable = false, length = 13)
    private String isbn;

    @Column(name = "title", nullable = false, length = Integer.MAX_VALUE)
    private String title;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "edition", length = 30)
    private String edition;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "cover", columnDefinition = "cover_type", nullable = false)
    private CoverType cover;

    @Column(name = "page_num", nullable = false)
    private Integer pageNum;

    @Column(name = "publisher", nullable = false, length = 50)
    private String publisher;

    @Type(Size2DUserType.class)
    @Column(name = "physical_size", columnDefinition = "size_2d_type")
    private Size2D physicalSize;

    @Type(value = NameTypeArrayUserType.class)
    @Column(name = "authors", nullable = false, columnDefinition = "name_type[]")
    private List<PersonName> authors = new ArrayList<>();

    @ColumnDefault("0")
    @Column(name = "stock")
    private Short stock;

}
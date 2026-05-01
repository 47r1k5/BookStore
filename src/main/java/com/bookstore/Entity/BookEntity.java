package com.bookstore.Entity;

import com.bookstore.Enum.CoverEnum;
import com.bookstore.POJOs.NamePOJO;
import com.bookstore.POJOs.Size2dPOJO;
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
@Table(name = "book")
public class BookEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 6165673022371847468L;
    @Id
    @SequenceGenerator(name = "book_id_gen", sequenceName = "bookgenre_bg_id_seq", allocationSize = 1)
    @Column(name = "isbn", nullable = false, length = 13)
    private String isbn;

    @Column(name = "title", nullable = false, length = Integer.MAX_VALUE)
    private String title;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "edition", length = 30)
    private String edition;

    @Column(name = "cover", columnDefinition = "cover_type not null")
    private CoverEnum cover;

    @Column(name = "page_num", nullable = false)
    private Integer pageNum;

    @Column(name = "publisher", nullable = false, length = 50)
    private String publisher;

    @Column(name = "physical_size", columnDefinition = "size_2d_type")
    private Size2dPOJO physicalSize;

    @Column(name = "authors", columnDefinition = "name_type[] not null")
    private NamePOJO authors;

    @ColumnDefault("0")
    @Column(name = "stock")
    private Short stock;

    @OneToMany(mappedBy = "bookIsbn")
    private Set<BookgenreEntity> bookgenres = new LinkedHashSet<>();


}
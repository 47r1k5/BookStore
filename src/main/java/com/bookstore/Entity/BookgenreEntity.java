package com.bookstore.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "bookgenre")
public class BookgenreEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = -3782125225879043153L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bookgenre_id_gen")
    @SequenceGenerator(name = "bookgenre_id_gen", sequenceName = "bookgenre_bg_id_seq", allocationSize = 1)
    @Column(name = "bg_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_isbn")
    private BookEntity bookIsbn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id")
    private GenreEntity genre;


}
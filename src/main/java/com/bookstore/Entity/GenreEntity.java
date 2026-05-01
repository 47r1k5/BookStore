package com.bookstore.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "genre")
public class GenreEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 8697315221232096737L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "genre_id_gen")
    @SequenceGenerator(name = "genre_id_gen", sequenceName = "genre_genre_id_seq", allocationSize = 1)
    @Column(name = "genre_id", nullable = false)
    private Integer id;

    @Column(name = "genre_name", length = 20)
    private String genreName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_genre_id")
    private GenreEntity mainGenre;

    @OneToMany(mappedBy = "genre")
    private Set<BookgenreEntity> bookgenres = new LinkedHashSet<>();

    @OneToMany(mappedBy = "mainGenre")
    private Set<GenreEntity> genres = new LinkedHashSet<>();

    @OneToMany(mappedBy = "genre")
    private Set<MoviegenreEntity> moviegenres = new LinkedHashSet<>();

    @OneToMany(mappedBy = "genre")
    private Set<MusicgenreEntity> musicgenres = new LinkedHashSet<>();


}
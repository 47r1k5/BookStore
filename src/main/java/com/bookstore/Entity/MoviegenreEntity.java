package com.bookstore.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "moviegenre")
public class MoviegenreEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 5363165345434720455L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "moviegenre_id_gen")
    @SequenceGenerator(name = "moviegenre_id_gen", sequenceName = "moviegenre_mg_id_seq", allocationSize = 1)
    @Column(name = "mg_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    private MovieEntity movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id")
    private GenreEntity genre;


}
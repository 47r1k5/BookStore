package com.bookstore.Entity;

import com.bookstore.CompositeType.NameTypeArrayUserType;
import com.bookstore.CompositeType.PersonName;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Type;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "movie")
public class MovieEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 3829815320137899033L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "movie_id_gen")
    @SequenceGenerator(name = "movie_id_gen", sequenceName = "movie_movie_id_seq", allocationSize = 1)
    @Column(name = "movie_id", nullable = false)
    private Integer id;

    @Column(name = "title", nullable = false, length = Integer.MAX_VALUE)
    private String title;

    @Type(value = NameTypeArrayUserType.class)
    @Column(name = "director", columnDefinition = "name_type[] not null")
    private List<PersonName> director= new ArrayList<>();

    @Column(name = "release_year", nullable = false)
    private Short releaseYear;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "length_min", nullable = false)
    private Short lengthMin;

    @ColumnDefault("0")
    @Column(name = "stock")
    private Short stock;

    public MovieEntity(String title, List<PersonName> director, Short releaseYear, Integer price, Short lengthMin, Short stock) {
        this.title = title;
        this.director = director;
        this.releaseYear = releaseYear;
        this.price = price;
        this.lengthMin = lengthMin;
        this.stock = stock;
    }
}
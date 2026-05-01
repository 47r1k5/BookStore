package com.bookstore.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "music")
public class MusicEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = -1192656700290871474L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "music_id_gen")
    @SequenceGenerator(name = "music_id_gen", sequenceName = "music_music_id_seq", allocationSize = 1)
    @Column(name = "music_id", nullable = false)
    private Integer id;

    @Column(name = "title", nullable = false, length = 30)
    private String title;

    @Column(name = "release_year", nullable = false)
    private Short releaseYear;

    @Column(name = "price", nullable = false)
    private Integer price;

    @ColumnDefault("0")
    @Column(name = "stock")
    private Short stock;

    @Column(name = "artist")
    private List<String> artist;

    @OneToMany(mappedBy = "music")
    private Set<MusicgenreEntity> musicgenres = new LinkedHashSet<>();


}
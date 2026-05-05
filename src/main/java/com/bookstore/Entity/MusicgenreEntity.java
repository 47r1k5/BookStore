package com.bookstore.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "musicgenre")
public class MusicgenreEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = -6516354697534262129L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "musicgenre_id_gen")
    @SequenceGenerator(name = "musicgenre_id_gen", sequenceName = "musicgenre_mg_id_seq", allocationSize = 1)
    @Column(name = "mg_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "music_id")
    private MusicEntity music;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id")
    private GenreEntity genre;

    public MusicgenreEntity(MusicEntity music, GenreEntity genre){
        this.music = music;
        this.genre = genre;
    }
}
package net.cinemaApplication.backend.entity.movie;

import jakarta.persistence.*;
import lombok.*;
import net.cinemaApplication.backend.entity.movieSession.MovieSession;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "movies")
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    @Column(name = "movie_length")
    private double movieLength;

    @Enumerated(EnumType.STRING) //has only one genre (maybe should be many to many)
    private Genre genre;

    private int rating; //value 1-10

    @Column(name = "age_limit")
    @Enumerated(EnumType.STRING)
    private AgeLimit ageLimit;

    private String director; //maybe should be an entity

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    List<MovieSession> sessions = new ArrayList<>();
}

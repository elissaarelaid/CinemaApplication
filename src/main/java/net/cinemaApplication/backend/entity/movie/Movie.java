package net.cinemaApplication.backend.entity.movie;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import net.cinemaApplication.backend.entity.movieSession.MovieSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//firstly we add movie and later moviesessions
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "movies")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    @Column(name = "movie_length")
    private int movieLength; //in minutes

    @Enumerated(EnumType.STRING) //has only one genre (maybe should be many to many)
    private Genre genre;

    private int rating; //value 1-10

    @Column(name = "age_limit")
    @Enumerated(EnumType.STRING)
    private AgeLimit ageLimit;

    private String director; //maybe should be an entity

    @JsonIgnore
    @Nullable
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    List<MovieSession> sessions = new ArrayList<>();
}

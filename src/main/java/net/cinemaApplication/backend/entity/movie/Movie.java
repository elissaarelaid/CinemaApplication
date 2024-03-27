package net.cinemaApplication.backend.entity.movie;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
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
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Schema(description = "Represents movie that clients can watch in the cinema")
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "The unique identifier of the movie")
    private Long id;

    @Size(min = 1, max = 60, message = "Movie title length must be between 1 and 60 characters")
    @Schema(description = "Title of the movie")
    private String title;

    @Size(min = 1, max = 300, message = "Movie description length must be between 1 and 300 characters")
    @Schema(description = "Description of the movie")
    private String description;

    @Schema(description = "Movie length in minutes")
    @Min(value = 5, message = "Movie length has to be longer than 5 minutes")
    @Max(value = 300, message = "Movie length cannot be longer than 300 minutes")
    @Column(name = "movie_length")
    private int movieLength;

    @Schema(description = "Enum to represent a movie genre. Examples: ROMANCE, THRILLER, HORROR, ACTION, COMEDY")
    @Enumerated(EnumType.STRING)
    private Genre genre;

    @Schema(description = "Movie rating")
    @Min(value = 1, message = "Rating must be over 1")
    @Max(value = 10, message = "Rating bust be smaller than 10")
    private int rating;

    @Column(name = "age_limit")
    @Enumerated(EnumType.STRING)
    @Schema(description = "Enum to represent a age limit. Examples: FAMILY, NO_LIMIT, OVER_16, OVER_14")
    private AgeLimit ageLimit;

    @Schema(description = "Movie director")
    @Size(min = 1, max = 50, message = "Movie director's name length must be between 1 and 50 characters")
    private String director;

    @Schema(description = "List of sessions related to this movie")
    @JsonIgnore
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    List<MovieSession> sessions = new ArrayList<>();
}

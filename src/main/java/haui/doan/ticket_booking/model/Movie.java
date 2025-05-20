package haui.doan.ticket_booking.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "movie")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_id")
    private Integer movieId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "duration", nullable = false)
    private Integer duration;

    @Column(name = "poster_url", nullable = false)
    private String posterUrl;

    @Column(name = "backdrop_path", nullable = false)
    private String backdropPath;

    @Column(name = "release_date", nullable = false)
    private String releaseDate;

    @Column(name = "language", nullable = false, length = 50)
    private String language;

    @Column(name = "tmdb", nullable = false)
    private double tmdb;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "ENUM('COMING_SOON','NOW_SHOWING','FINISHED_SHOWING')")
    private Status status;

    @Column(name = "tmdb_id")
    private Integer tmdbId;

    @Column(name = "type")
    private String type;

    @ManyToMany
    @JoinTable(
            name = "moviegenre",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private List<Genre> genres;

    @ManyToMany
    @JoinTable(
            name = "movieactor",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "actor_id")
    )
    private List<Actor> actors;
    
    @ManyToMany
    @JoinTable(
            name = "moviedirector",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "director_id")
    )
    private List<Director> directors;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MovieRating> ratings;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShowTime> showTimes;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FavoriteMovie> favoriteMovies;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MovieComment> comments;

    public enum Status {
        UNDETERMINED,
        COMING_SOON,
        NOW_SHOWING,
        FINISHED_SHOWING,
        SPECIAL
    }
}

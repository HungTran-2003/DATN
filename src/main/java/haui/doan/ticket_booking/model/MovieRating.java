package haui.doan.ticket_booking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "movierating",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "movie_id"}, name = "unique_user_movie_rating")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieRating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rating_id")
    private Integer ratingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @Column(name = "rating_value", nullable = false, precision = 2, scale = 1)
    private BigDecimal ratingValue;

    @Column(name = "rating_date")
    private LocalDateTime ratingDate = LocalDateTime.now();
}
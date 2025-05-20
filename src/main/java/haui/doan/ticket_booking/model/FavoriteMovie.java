package haui.doan.ticket_booking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "userfavoritemovies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteMovie {

    @EmbeddedId
    private FavoriteMovieId id;

    @Column(name = "added_date")
    private Timestamp addedDate;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("movieId")
    @JoinColumn(name = "movie_id")
    private Movie movie;


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FavoriteMovieId implements Serializable {
        private Integer userId;
        private Integer movieId;

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;

            FavoriteMovieId that = (FavoriteMovieId) obj;

            if (!userId.equals(that.userId)) return false;
            return movieId.equals(that.movieId);
        }

        @Override
        public int hashCode() {
            int result = userId.hashCode();
            result = 31 * result + movieId.hashCode();
            return result;
        }
    }
}

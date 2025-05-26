package haui.doan.ticket_booking.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import haui.doan.ticket_booking.model.FavoriteMovie;
import haui.doan.ticket_booking.model.Movie;
import haui.doan.ticket_booking.model.User;
import haui.doan.ticket_booking.model.Movie.Status;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Integer> {
    @Query("SELECT AVG(mr.ratingValue) FROM MovieRating mr WHERE mr.movie.movieId = :movieId")
    Optional<BigDecimal> findAverageRatingByMovieId(@Param("movieId") Integer movieId);

    @Query("SELECT m FROM Movie m WHERE LOWER(m.name) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    List<Movie> searchMovie(@Param("searchText") String searchText);

    @Modifying
    @Query("UPDATE Movie m SET m.status = :status WHERE m.movieId = :movieId")
    void updateMovieStatus(@Param("movieId") Integer movieId, @Param("status") Movie.Status status);

    @Query("SELECT DISTINCT m FROM Movie m LEFT JOIN FETCH m.showTimes")
    List<Movie> findAllWithShowTimes();

   @Query("SELECT m.movieId, m.posterUrl, m.name, m.duration, " +
       "AVG(mr.ratingValue) AS avgRating, COUNT(DISTINCT mr.id) AS ratingCount, MAX(st.date) AS latestShowDate " +
       "FROM Movie m " +
       "LEFT JOIN MovieRating mr ON m.movieId = mr.movie.movieId " +
       "LEFT JOIN ShowTime st ON m.movieId = st.movie.movieId " +
       "WHERE m.status = :status " +
       "GROUP BY m.movieId, m.posterUrl, m.name, m.duration " +
       "ORDER BY latestShowDate ASC")
    List<Object[]> getMovieByDate(@Param("status") Status status);

    @Query("SELECT " +
       " (SELECT COALESCE(AVG(mr.ratingValue), 0) FROM MovieRating mr WHERE mr.movie.movieId = :movieId), " +
       " (SELECT COUNT(f) FROM FavoriteMovie f WHERE f.movie.movieId = :movieId) " +
       "FROM Movie m WHERE m.movieId = :movieId")
    List<Object[]> getStatistics(@Param("movieId") Integer movieId);

    @Query("SELECT COUNT(f) FROM FavoriteMovie f WHERE f.movie.movieId = :movieId")
   Long testFavorite(@Param("movieId") Integer movieId);



   @Query("SELECT DISTINCT m FROM Movie m " +
           "JOIN m.genres g " +
           "WHERE (:name IS NULL OR LOWER(m.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
           "AND (:releaseDate IS NULL OR m.releaseDate = :releaseDate) " +
           "AND (:status IS NULL OR m.status = :status) " +
           "AND (:type IS NULL OR m.type = :type) " +
           "AND (:genreIds IS NULL OR g.genreId IN :genreIds)")
   List<Movie> fliterMovie(
           @Param("name") String name,
           @Param("releaseDate") String releaseDate,
           @Param("status") Status status,
           @Param("type") String type,
           @Param("genreIds") List<Integer> genreIds
   );      

   @Query("SELECT u.userId, u.name, rt.ratingId, rt.ratingValue, cm.commentId, cm.commentText " +
            "FROM Movie m " +
            "JOIN m.ratings rt " +
            "LEFT JOIN m.comments cm ON cm.user = rt.user AND cm.movie = m " +
            "JOIN rt.user u " +
            "WHERE m.movieId = :movieId")
   List<Object[]> getReviewMovie(@Param("movieId") Integer movieId);

   @Query("SELECT fr FROM FavoriteMovie fr " +
       "WHERE fr.user = :user " +
       "ORDER BY fr.addedDate")
   List<FavoriteMovie> getMovieFavortive(@Param("user") User user);
}
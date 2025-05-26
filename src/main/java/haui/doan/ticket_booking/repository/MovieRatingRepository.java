package haui.doan.ticket_booking.repository;

import haui.doan.ticket_booking.DTO.ReviewDetailDTO;
import haui.doan.ticket_booking.model.Movie;
import haui.doan.ticket_booking.model.MovieRating;
import haui.doan.ticket_booking.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRatingRepository extends JpaRepository<MovieRating, Integer> {

    @Query("SELECT new haui.doan.ticket_booking.DTO.ReviewDetailDTO(rt.ratingId, rt.ratingValue, cm.commentId, cm.commentText) " +
            "FROM MovieRating rt " +
            "LEFT JOIN MovieComment cm ON cm.user = rt.user AND cm.movie = rt.movie " +
            "WHERE rt.user = :user AND rt.movie = :movie")
    ReviewDetailDTO getReviewDetail(@Param("user") User user, @Param("movie") Movie movie);
}

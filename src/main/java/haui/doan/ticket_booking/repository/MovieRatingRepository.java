package haui.doan.ticket_booking.repository;

import haui.doan.ticket_booking.model.MovieRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRatingRepository extends JpaRepository<MovieRating, Integer> {
}

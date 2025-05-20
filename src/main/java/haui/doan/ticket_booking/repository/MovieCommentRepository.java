package haui.doan.ticket_booking.repository;

import haui.doan.ticket_booking.model.MovieComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieCommentRepository extends JpaRepository<MovieComment, Integer> {
}

package haui.doan.ticket_booking.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import haui.doan.ticket_booking.model.FavoriteMovie;

@Repository
public interface FavoriteMovieRep extends JpaRepository<FavoriteMovie, FavoriteMovie.FavoriteMovieId> {
    
}
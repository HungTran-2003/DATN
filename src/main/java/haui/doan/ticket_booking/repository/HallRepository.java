package haui.doan.ticket_booking.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import haui.doan.ticket_booking.model.Hall;

public interface HallRepository extends JpaRepository<Hall, Integer> {
    @Query("SELECT h.hallId, h.name, h.capacity FROM Hall h WHERE h.cinema.cinemaId = :cinemaId")
    List<Object[]> findHallsByCinemaId(@Param("cinemaId") Integer cinemaId);

    @Query("SELECT h.hallId, h.name, h.capacity FROM Hall h "+
           "JOIN h.showTimes st " +
            "WHERE st.id = :showtimeId "
           )
    Object findHallByShowtimeId(@Param("showtimeId") Integer showtimeId);
}
package haui.doan.ticket_booking.repository;

import haui.doan.ticket_booking.model.ShowTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;

@Repository
public interface ShowTimeRepository extends JpaRepository<ShowTime, Integer> {
    @Query("SELECT COUNT(s) FROM ShowTime s " +
           "WHERE s.hall.id = :hallId " +
           "AND s.date = :date " +
           "AND NOT (s.endTime <= :newStartTime OR s.startTime >= :newEndTime)")
    long countConflictingShowtimes(
        @Param("hallId") Integer hallId,
        @Param("date") Date date,
        @Param("newStartTime") Date newStartTime,
        @Param("newEndTime") Date newEndTime
    );
    
    @Query("SELECT st.id, st.startTime, st.status, " +
       "m.name, m.posterUrl, m.duration, st.ticketPrice, m.movieId, " +
       "COUNT(t.id) " +
       "FROM ShowTime st " +
       "JOIN st.movie m " +
       "LEFT JOIN st.bookings b " +
       "LEFT JOIN b.tickets t " +
       "GROUP BY st.id, st.startTime, st.status, " +
       "m.name, m.posterUrl, m.duration, st.ticketPrice, m.movieId")
    List<Object[]> findShowTimeWithMovieAndBookingCount();

    @Query("SELECT st.id, st.startTime, st.endTime, c.name, st.hall.hallId, st.hall.name, st.ticketPrice " +
            "FROM ShowTime st " +
            "JOIN Hall h ON h.hallId = st.hall.hallId " +
            "JOIN Cinema c ON c.cinemaId = h.cinema.cinemaId " +
            "WHERE st.status = :status AND st.movie.movieId = :movieId" )
    List<Object[]> getShowtimeByMovie(
            @Param("movieId") Integer movieId,
            @Param("status") ShowTime.Status status);

    @Query("SELECT COUNT(s) FROM ShowTime s " +
           "WHERE s.hall.hallId = :hallId " +
           "AND s.date = :showDate " +
           "AND s.id != :currentId " +
           "AND ((s.startTime BETWEEN :startTime AND :endTime) " +
           "OR (s.endTime BETWEEN :startTime AND :endTime) " +
           "OR (s.startTime <= :startTime AND s.endTime >= :endTime))")
    long countConflictingShowtimesExcludingCurrent(
            @Param("hallId") Integer hallId,
            @Param("showDate") Date showDate,
            @Param("startTime") Date startTime,
            @Param("endTime") Date endTime,
            @Param("currentId") Integer currentId
    );
}
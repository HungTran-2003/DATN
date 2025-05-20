package haui.doan.ticket_booking.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import haui.doan.ticket_booking.model.Ticket;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {

    @Query("SELECT s.seatId FROM Ticket t " +
            "JOIN t.seat s " +
            "JOIN t.booking b " +
            "Join b.showTime st " +
            "WHERE st.id = :showTimeId")
    List<Object[]> getSeatsByShowtimeId(Integer showTimeId);

    @Query("SELECT t.ticketId, t.ticketCode, t.price, " +
           "m.name as movieName, m.posterUrl, " +
           "st.startTime, st.endTime, " +
           "c.name as cinemaName, " +
           "h.name as hallName, " +
           "s.rowNumbers, s.seatNumbers " +
           "FROM Ticket t " +
           "JOIN t.booking b " +
           "JOIN b.showTime st " +
           "JOIN st.movie m " +
           "JOIN st.hall h " +
           "JOIN h.cinema c " +
           "JOIN t.seat s " +
           "WHERE b.user.userId = :userId " +
           "AND t.ticketStatus = 'UNUSED'")
    List<Object[]> findUnusedTicketsByUserId(@Param("userId") Integer userId);

    @Query("SELECT t FROM Ticket t " +
           "JOIN t.booking b " +
           "JOIN b.showTime st " +
           "WHERE t.ticketStatus = 'UNUSED' " +
           "AND st.endTime < :currentTime")
    List<Ticket> findExpiredUnusedTickets(@Param("currentTime") Date currentTime);

    @Query("SELECT t.ticketId, t.ticketCode, t.price, " +
           "m.name as movieName, m.posterUrl, " +
           "st.startTime, st.endTime, " +
           "c.name as cinemaName, " +
           "h.name as hallName, " +
           "s.rowNumbers, s.seatNumbers "  +
           "FROM Ticket t " +
           "JOIN t.booking b " +
           "JOIN b.showTime st " +
           "JOIN st.movie m " +
           "JOIN st.hall h " +
           "JOIN h.cinema c " +
           "JOIN t.seat s " +
           "WHERE t.booking.user.userId = :userId AND t.booking.bookingId = :bookingId")
    List<Object[]> findTicketsByUserAndBooking(
        @Param("userId") Integer userId, 
        @Param("bookingId") Integer bookingId
    );
}

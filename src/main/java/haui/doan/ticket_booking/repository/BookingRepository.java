package haui.doan.ticket_booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import haui.doan.ticket_booking.model.Booking;
import haui.doan.ticket_booking.model.User;

import java.util.List;


@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    
    @Query("SELECT COALESCE(COUNT(t.ticketId), 0) as ticketCount, st.maxTicket " +
       "FROM Booking b " +
       "LEFT JOIN b.tickets t " +
        "JOIN b.showTime st " +
       "WHERE b.showTime.Id = :showTimeId AND b.user.userId = :userId " +
       "GROUP BY st.maxTicket")
    Object[] getTicketCountAndMaxTicketByUserAndShowtime(
        @Param("userId") Integer userId,
        @Param("showTimeId") Integer showTimeId
    );

    List<Booking> findByUser(User user);
    
    @Query("SELECT DISTINCT b FROM Booking b " +
           "JOIN b.tickets t " +
           "WHERE b.paymentStatus = 'PAID' AND b.user = :user " +
           "AND EXISTS (SELECT 1 FROM Ticket t2 WHERE t2.booking = b AND t2.ticketStatus = 'USED')")
    List<Booking> findPaidBookingsWithUsedTickets(User user);

    @Query("SELECT b FROM Booking b " +
           "LEFT JOIN b.showTime st " +
           "WHERE st.endTime > CURRENT_DATE AND b.user = :user ")
    List<Booking> findBookingNotShowing(User user);       

}

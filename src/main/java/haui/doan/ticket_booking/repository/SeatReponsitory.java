package haui.doan.ticket_booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import haui.doan.ticket_booking.model.Seat;

@Repository
public interface SeatReponsitory extends JpaRepository<Seat, Integer> {
}

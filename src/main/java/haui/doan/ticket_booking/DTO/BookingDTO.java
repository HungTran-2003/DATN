package haui.doan.ticket_booking.DTO;

import java.math.BigDecimal;
import java.util.List;

import haui.doan.ticket_booking.model.Booking;
import haui.doan.ticket_booking.model.Ticket;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class BookingDTO {
    private Integer bookingId;
    private Integer userId;
    private Integer showtimeId;
    private String startTime;
    private String endTime;
    private String movieName;
    private String posterUrl;
    private String cinemaName;
    private String hallName;
    private List<Integer> seatIds;
    private BigDecimal totalPrice;
    private List<SeatDTO> seats;
    private String status;

    public BookingDTO(Booking booking) {
        this.bookingId = booking.getBookingId();
        this.userId = booking.getUser().getUserId();
        this.showtimeId = booking.getShowTime().getId();
        this.startTime = booking.getShowTime().getStartTime().toString();
        this.endTime = booking.getShowTime().getEndTime().toString();
        this.movieName = booking.getShowTime().getMovie().getName();
        this.posterUrl = booking.getShowTime().getMovie().getPosterUrl();
        this.totalPrice = booking.getTotalPrice();
        this.cinemaName = booking.getShowTime().getHall().getCinema().getName();
        this.hallName = booking.getShowTime().getHall().getName();
        this.seats = booking.getTickets().stream()
                .map(SeatDTO::new)
                .toList();
        this.status = booking.getPaymentStatus().toString();
    }
}

@Getter
@Setter
class SeatDTO {
    private Integer seatId;
    private String seatName;

    public SeatDTO(Ticket ticket) {
        this.seatId = ticket.getSeat().getSeatId();
        this.seatName = ticket.getSeat().getRowNumbers() + ticket.getSeat().getSeatNumbers();
    }
}

@Getter
@Setter
class BookingCreateDTO {
    private Integer userId;
    private Integer showtimeId;
    private List<Integer> seatIds;
    private BigDecimal totalPrice;
}
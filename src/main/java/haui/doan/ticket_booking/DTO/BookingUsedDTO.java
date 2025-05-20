package haui.doan.ticket_booking.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingUsedDTO {
    private Integer bookingId;
    private String startTime;
    private String endTime;
    private String movieName;
    private String posterUrl;
    private String cinemaName;
    private String hallName;
    private String status;

    public BookingUsedDTO(Integer bookingId, String startTime, String endTime, String movieName, String posterUrl, String cinemaName, String hallName, String status) {
        this.bookingId = bookingId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.movieName = movieName;
        this.posterUrl = posterUrl;
        this.cinemaName = cinemaName;
        this.hallName = hallName;
        this.status = status;
    }
}

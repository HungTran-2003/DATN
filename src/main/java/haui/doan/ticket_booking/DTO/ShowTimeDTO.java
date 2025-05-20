package haui.doan.ticket_booking.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ShowTimeDTO {
    private String startTime;
    private String endTime;
    private Integer hallId;
    private Integer movieId;
    private Integer cinemaId;
    private String date;
    private String price;
    private Integer maxTicket;
}

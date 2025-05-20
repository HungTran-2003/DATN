package haui.doan.ticket_booking.DTO;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class MovieReviewDTO {
    private Integer movieId;
    private Integer userId;  
    private BigDecimal rating;
    private String comment; 
}

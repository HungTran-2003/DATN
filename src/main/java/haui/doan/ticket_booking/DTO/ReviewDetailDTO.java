package haui.doan.ticket_booking.DTO;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDetailDTO {
    private Integer rattingId;
    private BigDecimal rating;
    private Integer commentId;
    private String comment; 

}

package haui.doan.ticket_booking.DTO;

import java.sql.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import haui.doan.ticket_booking.model.Cinema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CouponDTO {
    private Integer couponId;
    private String name;
    private Double discount;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private Date expirationDate;
    private String description;
    private List<Integer> cinemaIds;
    private int amount;
    private String code;
    private String status;
    private int totalUsed;

    private List<CinemaDTO> cinemas;
}

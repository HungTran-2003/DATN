package haui.doan.ticket_booking.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CreateLinkPaymentDTO {
    
    private long orderCode;
    private String productName;
    private String description;
    private String returnUrl;
    private int price;
    private String cancelUrl;
}

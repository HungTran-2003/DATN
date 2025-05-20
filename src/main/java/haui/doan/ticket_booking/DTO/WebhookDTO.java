package haui.doan.ticket_booking.DTO;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WebhookDTO {
    private String code;
    private String desc;
    private Map<String, Object> data;
    private String signature;
}

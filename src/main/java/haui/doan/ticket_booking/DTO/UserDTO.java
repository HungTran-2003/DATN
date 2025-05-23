package haui.doan.ticket_booking.DTO;

import java.math.BigDecimal;

import haui.doan.ticket_booking.model.Booking;
import haui.doan.ticket_booking.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Integer userId;
    private String name;
    private String email;
    private String phoneNumber;
    private String registrationDate;
    private String accountStatus;
    private Integer totalPay;

    public UserDTO(User user) {
        this.userId = user.getUserId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.registrationDate = user.getRegistrationDate().toString();
        this.accountStatus = user.getAccountStatus().name();
        BigDecimal totalPay = user.getBookings().stream().filter(booking -> booking.getPaymentStatus().equals(Booking.PaymentStatus.PAID))
                .map(booking -> booking.getTotalPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.totalPay = totalPay.intValue();
    }
}

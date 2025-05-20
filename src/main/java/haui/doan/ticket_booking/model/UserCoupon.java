package haui.doan.ticket_booking.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "usercoupon")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserCoupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "coupon_id", referencedColumnName = "coupon_id")
    private Coupon coupon;

    @Column(name = "time_used", nullable = false)
    private LocalDateTime timeUsed;
}

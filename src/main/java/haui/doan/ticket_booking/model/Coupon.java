package haui.doan.ticket_booking.model;

import java.sql.Date;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "coupon")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_id")
    private Integer couponId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "discount", nullable = false)
    private Double discount;

    @Column(name = "expiration_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date expirationDate;

    @Column(name = "description")
    private String description;

    @Column(name = "code", unique = true, nullable = false)
    private String code;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "amount", nullable = false)
    private Integer amount;

    @ManyToMany
    @JoinTable(
            name = "cinemacoupon",
            joinColumns = @JoinColumn(name = "coupon_id"),
            inverseJoinColumns = @JoinColumn(name = "cinema_id")
    )
    private List<Cinema> cinemas;

    @OneToMany(mappedBy = "coupon", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserCoupon> userCoupons;

    public enum Status {
        ACTIVATE,
        INACTIVATE
    }


}

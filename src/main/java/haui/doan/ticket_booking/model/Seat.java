package haui.doan.ticket_booking.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "seat")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Seat {

    @Id
    @Column(name = "seat_id")
    private Integer seatId;

    @Column(name = "row_numbers", nullable = false)
    private String rowNumbers;

    @Column(name = "seat_number", nullable = false)
    private String seatNumbers;

    @Enumerated(EnumType.STRING)
    @Column(name = "seat_type", nullable = false, columnDefinition = "ENUM('Standard','VIP')")
    private TypeSeat typeSeat;

    @ManyToOne
    @JoinColumn(name = "hall_id", nullable = false)
    private Hall hall;

    public enum TypeSeat{
        Standard,
        VIP
    }
}

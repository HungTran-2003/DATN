package haui.doan.ticket_booking.DTO;

import java.util.List;

import haui.doan.ticket_booking.model.Cinema;
import haui.doan.ticket_booking.model.Hall;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CinemaDTO {

    private Integer cinemaId;
    private String name;
    private String address;
    private String contactInfo;
    private List<HallDTO> halls;

    public CinemaDTO(Cinema cinema) {
        this.cinemaId = cinema.getCinemaId();
        this.name = cinema.getName();
        this.address = cinema.getAddress();
        this.contactInfo = cinema.getContactInfo();
        this.halls = cinema.getHalls().stream()
                .map(HallDTO::new)
                .toList();
    }

}

@Getter
@Setter
class HallDTO {
    private Integer hallId;
    private String name;
    private Integer capacity;

    public HallDTO(Hall hall) {
        this.hallId = hall.getHallId();
        this.name = hall.getName();
        this.capacity = hall.getCapacity();
    }
}



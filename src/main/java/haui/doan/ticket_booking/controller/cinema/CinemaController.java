package haui.doan.ticket_booking.controller.cinema;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import haui.doan.ticket_booking.DTO.CinemaDTO;
import haui.doan.ticket_booking.model.Cinema;
import haui.doan.ticket_booking.model.Hall;
import haui.doan.ticket_booking.service.cinema.CinemaService;
import haui.doan.ticket_booking.service.cinema.HallService;


@RestController
@RequestMapping("/api/cinemas")
public class CinemaController {

    @Autowired
    private CinemaService cinemaService;

    @Autowired
    private HallService hallService;

    @GetMapping("/by-city/{cityId}")
    public List<Object> getCinemasByCityId(@PathVariable Integer cityId) {
        return cinemaService.getCinemasByCityId(cityId);
    }

    @GetMapping("/getAll")
    public List<CinemaDTO> getAllCinema() {
        List<Cinema> cinemas = cinemaService.getAllCinema();
        return cinemas.stream()
                  .map(CinemaDTO::new)
                  .collect(Collectors.toList());
    }

     @GetMapping("/getHall/{cinemaId}")
    public List<Object> getHallsByCinemaId(@PathVariable Integer cinemaId) {
        return hallService.getHallsByCinemaId(cinemaId);
    }

    @GetMapping("/getHallByShowtime/{showtimeId}")
    public Object getHallByShowtimeId(@PathVariable Integer showtimeId) {
        return hallService.getHallByShowtimeId(showtimeId);
    }
    
}
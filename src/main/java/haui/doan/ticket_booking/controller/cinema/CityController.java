package haui.doan.ticket_booking.controller.cinema;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import haui.doan.ticket_booking.model.City;
import haui.doan.ticket_booking.service.cinema.CityService;

@RestController
@RequestMapping("/api/cities")
public class CityController {
    @Autowired
    private CityService cityService;

    @GetMapping
    public List<City> getAllCities() {
        return cityService.getAllCities();
    }
}

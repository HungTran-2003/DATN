package haui.doan.ticket_booking.service.cinema;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import haui.doan.ticket_booking.model.City;
import haui.doan.ticket_booking.repository.CityRepository;

@Service
public class CityService {
    @Autowired
    private CityRepository cityRepository;

    public List<City> getAllCities() {
        return cityRepository.findAll();
    }

}

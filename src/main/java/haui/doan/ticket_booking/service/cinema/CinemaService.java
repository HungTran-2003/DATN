package haui.doan.ticket_booking.service.cinema;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import haui.doan.ticket_booking.model.Cinema;
import haui.doan.ticket_booking.repository.CinemaRepository;

@Service
public class CinemaService {

    @Autowired
    private CinemaRepository cinemaRepository;

    public List<Object> getCinemasByCityId(Integer cityId) {
        List<Object[]> results = cinemaRepository.findCinemasByCityIdNative(cityId);
        return results.stream()
                .map(row -> {
                    return new Object() {
                        public int cinemaId = (Integer) row[0];
                        public String name = (String) row[1];
                    };
                })
                .collect(Collectors.toList());
    }

    public List<Cinema> getAllCinema() {
        List<Cinema> results = cinemaRepository.findAll();
        return results;
    }
}

package haui.doan.ticket_booking.service.cinema;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import haui.doan.ticket_booking.model.Hall;
import haui.doan.ticket_booking.repository.HallRepository;

@Service
public class HallService {
    @Autowired
    private HallRepository hallRepository;

    public List<Object> getHallsByCinemaId(Integer cinemaId) {
        List<Object[]> results = hallRepository.findHallsByCinemaId(cinemaId);
        return results.stream()
                .map(row -> {
                    return new Object() {
                        public Integer hallId = (Integer) row[0];
                        public String name = (String) row[1];
                    };
                })
                .collect(Collectors.toList());
    }

    public Object getHallByShowtimeId(Integer showtimeId) {
        Object result = hallRepository.findHallByShowtimeId(showtimeId);
        if (result != null) {
            return new Object() {
                public Integer hallId = (Integer) ((Object[]) result)[0];
                public String name = (String) ((Object[]) result)[1];
                public Integer capacity = (Integer) ((Object[]) result)[2];
            };
        } else {
            return null;
        }
    }
}
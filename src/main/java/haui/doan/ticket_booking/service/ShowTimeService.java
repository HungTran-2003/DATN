package haui.doan.ticket_booking.service;

import haui.doan.ticket_booking.DTO.ShowTimeDTO;
import haui.doan.ticket_booking.model.Hall;
import haui.doan.ticket_booking.model.Movie;
import haui.doan.ticket_booking.model.Seat;
import haui.doan.ticket_booking.model.ShowTime;
import haui.doan.ticket_booking.model.ShowTime.Status;
import haui.doan.ticket_booking.repository.ShowTimeRepository;
import jakarta.transaction.Transactional;
import haui.doan.ticket_booking.repository.MovieRepository;
import haui.doan.ticket_booking.repository.HallRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class ShowTimeService {
    @Autowired
    private ShowTimeRepository showTimeRepository;
    
    @Autowired
    private MovieRepository movieRepository;
    
    @Autowired
    private HallRepository hallRepository;

    public List<Map<String, Object>> getSeatsByHallId(Integer hallId) {
        Hall hall = hallRepository.findById(hallId)
            .orElseThrow(() -> new RuntimeException("Hall not found"));

        List<Map<String, Object>> response = new ArrayList<>();
        for (Seat seat : hall.getSeats()) {
            Map<String, Object> seatInfo = new HashMap<>();
            seatInfo.put("seatId", seat.getSeatId());
            seatInfo.put("rowNumbers", seat.getRowNumbers());
            seatInfo.put("seatNumbers", seat.getSeatNumbers());
            seatInfo.put("typeSeat", seat.getTypeSeat());
            response.add(seatInfo);
        }
        return response;
    }

    @Transactional
    public ShowTime addShowTime(ShowTimeDTO showTimeDTO) throws ParseException {
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd-MM-yyyy-HH:mm", Locale.ENGLISH);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);

        Date startTime = dateTimeFormat.parse(showTimeDTO.getStartTime());
        Date endTime = dateTimeFormat.parse(showTimeDTO.getEndTime());
        Date showDate = dateFormat.parse(showTimeDTO.getDate());

        // Check for conflicts
        long conflictCount = showTimeRepository.countConflictingShowtimes(
            showTimeDTO.getHallId(),
            showDate,
            startTime,
            endTime
        );

        if (conflictCount > 0) {
            throw new RuntimeException("Không thể thêm lịch chiếu - đã có lịch chiếu khác trong khoảng thời gian này");
        }

        ShowTime showTime = new ShowTime();
        showTime.setStartTime(startTime);
        showTime.setEndTime(endTime);
        showTime.setDate(showDate);
        showTime.setTicketPrice(new BigDecimal(showTimeDTO.getPrice()));
        showTime.setMaxTicket(showTimeDTO.getMaxTicket());
        showTime.setStatus(ShowTime.Status.COMING_SOON);

        Movie movie = movieRepository.findById(showTimeDTO.getMovieId())
                .orElseThrow(() -> new RuntimeException("Movie not found"));
        
        showTime.setMovie(movie);
        if(movie.getStatus() != Movie.Status.SPECIAL){
            movieRepository.updateMovieStatus(showTimeDTO.getMovieId(), Movie.Status.NOW_SHOWING);
        }

        showTime.setHall(hallRepository.findById(showTimeDTO.getHallId())
                .orElseThrow(() -> new RuntimeException("Hall not found")));
                
        return showTimeRepository.save(showTime);
    }

    @Transactional
    public ShowTime updateShowTime(Integer id, ShowTimeDTO showTimeDTO) throws ParseException {
        ShowTime showTime = showTimeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ShowTime not found"));

        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd-MM-yyyy-HH:mm", Locale.ENGLISH);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);

        Date startTime = dateTimeFormat.parse(showTimeDTO.getStartTime());
        Date endTime = dateTimeFormat.parse(showTimeDTO.getEndTime());
        Date showDate = dateFormat.parse(showTimeDTO.getDate());

        // Check for conflicts excluding current showtime
        long conflictCount = showTimeRepository.countConflictingShowtimesExcludingCurrent(
            showTimeDTO.getHallId(),
            showDate,
            startTime,
            endTime,
            id
        );

        if (conflictCount > 0) {
            throw new RuntimeException("Không thể cập nhật lịch chiếu - đã có lịch chiếu khác trong khoảng thời gian này");
        }

        showTime.setStartTime(startTime);
        showTime.setEndTime(endTime);
        showTime.setDate(showDate);
        showTime.setTicketPrice(new BigDecimal(showTimeDTO.getPrice()));
        showTime.setMaxTicket(showTimeDTO.getMaxTicket());
        
        if (!showTime.getMovie().getMovieId().equals(showTimeDTO.getMovieId())) {
            showTime.setMovie(movieRepository.findById(showTimeDTO.getMovieId())
                    .orElseThrow(() -> new RuntimeException("Movie not found")));
        }

        if (!showTime.getHall().getHallId().equals(showTimeDTO.getHallId())) {
            showTime.setHall(hallRepository.findById(showTimeDTO.getHallId())
                    .orElseThrow(() -> new RuntimeException("Hall not found")));
        }

        return showTimeRepository.save(showTime);
    }

    public List<Map<String, Object>> getShowTimeWithMovieAndBookingCount() {
        List<Object[]> results = showTimeRepository.findShowTimeWithMovieAndBookingCount();
        List<Map<String, Object>> response = new ArrayList<>();
        
        for (Object[] row : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", row[0]);

            // Convert Date to LocalDateTime and adjust to Asia/Ho_Chi_Minh timezone
            Date startTime = (Date) row[1]; 
            if (startTime != null) {
                map.put("startTime", formatDate(startTime));
            } else {
                map.put("startTime", null);
            }
            map.put("status", row[2]);
            map.put("movieName", row[3]);
            map.put("posterUrl", row[4]);
            map.put("duration", row[5]);
            map.put("ticketPrice", row[6]);
            map.put("movieId", row[7]);
            map.put("bookingCount", row[10]);
            map.put("hallId", row[8]);
            map.put("cinemaName", row[9]);

            response.add(map);
        }
        
        return response;
    }

    public List<Map<String, Object>> getShowtimeByMovie(Integer movieId, Status status) {
        List<Object[]> results = showTimeRepository.getShowtimeByMovie(movieId, status);
        List<Map<String, Object>> response = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", row[0]);
            map.put("startTime", formatDate((Date) row[1]));
            map.put("endTime", formatDate((Date) row[2]));
            map.put("cinemaName", row[3]);
            map.put("hallId", row[4]);
            map.put("hallName", row[5]);
            map.put("ticketPrice", row[6]);
            response.add(map);
        }
        
        return response;
    }

    private LocalDateTime formatDate(Date date) {
        LocalDateTime localStartTime = date.toInstant()
                    .atZone(ZoneId.of("Asia/Ho_Chi_Minh"))
                    .toLocalDateTime();
        return localStartTime;
    }
}
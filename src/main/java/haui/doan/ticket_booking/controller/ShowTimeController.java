package haui.doan.ticket_booking.controller;

import haui.doan.ticket_booking.DTO.ShowTimeDTO;
import haui.doan.ticket_booking.model.ShowTime;
import haui.doan.ticket_booking.model.ShowTime.Status;
import haui.doan.ticket_booking.service.ShowTimeService;
import haui.doan.ticket_booking.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/showtime")
public class ShowTimeController {
    
    @Autowired
    private ShowTimeService showTimeService;

    @Autowired
    private BookingService bookingService;

    @GetMapping("/seats/{hallId}")
    public ResponseEntity<?> getSeatsByHallId(@PathVariable Integer hallId) {
        try {
            List<Map<String, Object>> seats = showTimeService.getSeatsByHallId(hallId);
            return ResponseEntity.ok(seats);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @PostMapping("/add")
    public ResponseEntity<String> addShowTime(@RequestBody ShowTimeDTO showTimeDTO) {
        try {
            ShowTime showTime = showTimeService.addShowTime(showTimeDTO);
            if (showTime != null) {
                return ResponseEntity.ok("Thêm thành công");
            } else {
                return ResponseEntity.badRequest().body("Thêm thất bại");
                
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(e.getMessage());
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<Map<String, Object>>> getShowTimes() {
        return ResponseEntity.ok(showTimeService.getShowTimeWithMovieAndBookingCount());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getShowTimeById(@PathVariable Integer id) {
        try {
            List<Map<String, Object>> showTime = showTimeService.getShowtimeByMovie(id, Status.COMING_SOON);
            if (showTime != null) {
                return ResponseEntity.ok(showTime);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateShowTime(@PathVariable Integer id, @RequestBody ShowTimeDTO showTimeDTO) {
        try {
            ShowTime updatedShowTime = showTimeService.updateShowTime(id, showTimeDTO);
            if (updatedShowTime == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok("Cập nhật thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{showtimeId}/user/{userId}/tickets")
    public ResponseEntity<?> getShowTimeTicketsInfo(
            @PathVariable Integer showtimeId,
            @PathVariable Integer userId) {
        try {
            Map<String, Object> ticketInfo = bookingService.getTickUserByShowtime(userId, showtimeId);
            return ResponseEntity.ok(ticketInfo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
package haui.doan.ticket_booking.controller.user;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import haui.doan.ticket_booking.model.Booking;
import haui.doan.ticket_booking.repository.BookingRepository;
import haui.doan.ticket_booking.service.BookingService;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingRepository bookingRepository;

    public final Map<Integer, LocalDateTime> bookingPending = new LinkedHashMap<>();

    @Autowired
    private BookingService bookingService;

    BookingController(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @PostConstruct
    public void init() {
        List<Booking> bookings = bookingService.findBookingPending();
        for(Booking booking : bookings){
            bookingPending.put(booking.getBookingId(), booking.getBookingTime());
        }
    }

    public Map<Integer, LocalDateTime> getBookingPending() {
        return bookingPending;
    }

    @Transactional
    @PostMapping("/create")
    public ResponseEntity<?> createBooking(@RequestBody Map<String, Object> body) {
        Integer userId = (Integer) body.get("userId");
        Integer showtimeId = (Integer) body.get("showtimeId");
        BigDecimal totalPrice = new BigDecimal(body.get("totalPrice").toString());
        List<?> rawList = (List<?>) body.get("seatIds");
        List<Integer> seatIds = rawList.stream()
        .map(val -> Integer.parseInt(val.toString())).toList();
        String couponCode = (String) body.get("couponCode");
        System.out.println("c: " + couponCode);
        try {
            Booking booking = bookingService.createBooking(
                userId, showtimeId, seatIds, totalPrice, couponCode
            );
            
            Map<String, String> bookingId = Map.of("bookingId", booking.getBookingId().toString());
            bookingPending.put(booking.getBookingId(), booking.getBookingTime());
            return ResponseEntity.ok(bookingId);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating booking: " + e.getMessage());
        }
    }

    @GetMapping("/getAll/{userId}")
    public ResponseEntity<?> getAllBookings(@PathVariable Integer userId) {
        try {
            return ResponseEntity.ok(bookingService.getBookingDTOs(userId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching bookings: " + e.getMessage());
        }
    }

    @GetMapping("/getBookingUsed/{userId}")
    public ResponseEntity<?> getBookingsUsed(@PathVariable Integer userId) {
        try {
            return ResponseEntity.ok(bookingService.findPaidBookingsWithUsedTickets(userId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching bookings: " + e.getMessage());
        }
    }

    @GetMapping("/getBookingNotShowing/{userId}")
    public ResponseEntity<?> getBookingNotShowing(@PathVariable Integer userId) {
        try {
            return ResponseEntity.ok(bookingService.findBookingNotShowing(userId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching bookings: " + e.getMessage());
        }
    }

    @DeleteMapping("{bookingId}")
    public ResponseEntity<?> delete(@PathVariable Integer bookingId) {
        try {
            bookingService.deleteBooking(bookingId);
            return ResponseEntity.ok("ok");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching bookings: " + e.getMessage());
        }
    } 

    

}
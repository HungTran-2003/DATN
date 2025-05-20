package haui.doan.ticket_booking.controller.user;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import haui.doan.ticket_booking.service.TicketService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @GetMapping("/getSeatsByShowTimeId/{showTimeId}")
    public ResponseEntity<?> getMethodName(@PathVariable Integer showTimeId) {
        try {
            List<Map<String, Object>> seats = ticketService.getSeatsByShowtimeId(showTimeId);
            if (seats != null) {
                return ResponseEntity.ok(seats);
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

    @GetMapping("/unused/{userId}")
    public ResponseEntity<?> getUnusedTickets(@PathVariable Integer userId) {
        try {
            List<Map<String, Object>> tickets = ticketService.getUnusedTicketsByUserId(userId);
            return ResponseEntity.ok(tickets);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching unused tickets: " + e.getMessage());
        }
    }

    @GetMapping("/{userId}/{bookingId}")
    public ResponseEntity<?> getTicketsByUserAndBooking(
        @PathVariable Integer userId,
        @PathVariable Integer bookingId
    ) {
        try {
            List<Map<String, Object>> tickets = ticketService.getTicketsByUserAndBooking(userId, bookingId);
            return ResponseEntity.ok(tickets);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching tickets: " + e.getMessage());
        }
    }
}

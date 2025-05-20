package haui.doan.ticket_booking.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import haui.doan.ticket_booking.repository.TicketRepository;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    public List<Map<String, Object>> getSeatsByShowtimeId(Integer showTimeId) {
        List<Object[]> seats = ticketRepository.getSeatsByShowtimeId(showTimeId);
        List<Map<String, Object>> response = new ArrayList<>();

        for (Object[] seat : seats) {
            Map<String, Object> seatInfo = Map.of(
                "seatId", seat[0]
            );
            response.add(seatInfo);
        }
        return response;
    }

    public List<Map<String, Object>> getUnusedTicketsByUserId(Integer userId) {
        List<Object[]> tickets = ticketRepository.findUnusedTicketsByUserId(userId);
        List<Map<String, Object>> response = new ArrayList<>();

        for (Object[] ticket : tickets) {
            Map<String, Object> ticketInfo = new HashMap<>();
            ticketInfo.put("ticketId", ticket[0]);
            ticketInfo.put("ticketCode", ticket[1]);
            ticketInfo.put("price", ticket[2]);
            
            // Movie information
            ticketInfo.put("movieName", ticket[3]);
            ticketInfo.put("posterUrl", ticket[4]);
            
            // Showtime information
            ticketInfo.put("startTime", ticket[5]);
            ticketInfo.put("endTime", ticket[6]);
            
            // Cinema and Hall information
            ticketInfo.put("cinemaName", ticket[7]);
            ticketInfo.put("hallName", ticket[8]);
            
            // Seat information
            Map<String, String> seatInfo = new HashMap<>();
            seatInfo.put("row", (String) ticket[9]);
            seatInfo.put("number", (String) ticket[10]);
            ticketInfo.put("seat", seatInfo);

            response.add(ticketInfo);
        }
        return response;
    }

    public List<Map<String, Object>> getTicketsByUserAndBooking(Integer userId, Integer bookingId) {        
       List<Object[]> tickets = ticketRepository.findTicketsByUserAndBooking(userId, bookingId);
        List<Map<String, Object>> response = new ArrayList<>();

        for (Object[] ticket : tickets) {
            Map<String, Object> ticketInfo = new HashMap<>();
            ticketInfo.put("ticketId", ticket[0]);
            ticketInfo.put("ticketCode", ticket[1]);
            ticketInfo.put("price", ticket[2]);
            
            // Movie information
            ticketInfo.put("movieName", ticket[3]);
            ticketInfo.put("posterUrl", ticket[4]);
            
            // Showtime information
            ticketInfo.put("startTime", ticket[5]);
            ticketInfo.put("endTime", ticket[6]);
            
            // Cinema and Hall information
            ticketInfo.put("cinemaName", ticket[7]);
            ticketInfo.put("hallName", ticket[8]);
            
            // Seat information
            Map<String, String> seatInfo = new HashMap<>();
            seatInfo.put("row", (String) ticket[9]);
            seatInfo.put("number", (String) ticket[10]);
            ticketInfo.put("seat", seatInfo);

            response.add(ticketInfo);
        }
        return response;
    }
}

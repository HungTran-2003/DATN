package haui.doan.ticket_booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import haui.doan.ticket_booking.DTO.BookingDTO;
import haui.doan.ticket_booking.model.*;
import haui.doan.ticket_booking.repository.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShowTimeRepository showTimeRepository;

    @Autowired
    private SeatReponsitory seatRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Transactional
    public Booking createBooking(Integer userId, Integer showtimeId, List<Integer> seatIds, BigDecimal totalPrice, String couponCode) {
       
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        ShowTime showTime = showTimeRepository.findById(showtimeId)
                .orElseThrow(() -> new RuntimeException("Showtime not found"));

        Coupon coupon = null;

        System.out.println("hii");

        if (couponCode != null) {
            System.out.println("hii2");
            coupon = couponRepository.findByCode(couponCode)
                    .orElseThrow(() -> new RuntimeException("Coupon not found"));
            UserCoupon userCoupon = new UserCoupon();
            userCoupon.setUser(user);
            userCoupon.setCoupon(coupon);
            userCoupon.setTimeUsed(LocalDateTime.now());    
            userCouponRepository.save(userCoupon);
        }  
        
        System.out.println("hii1");

        // Create new booking
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setShowTime(showTime);
        booking.setBookingTime(LocalDateTime.now());
        booking.setTotalPrice(totalPrice);
        booking.setPaymentStatus(Booking.PaymentStatus.PENDING);

        // Create tickets for each seat
        List<Ticket> tickets = new ArrayList<>();
        for (Integer seatId : seatIds) {
            Seat seat = seatRepository.findById(seatId)
                    .orElseThrow(() -> new RuntimeException("Seat not found: " + seatId));

            Ticket ticket = new Ticket();
            ticket.setBooking(booking);
            ticket.setSeat(seat);
            ticket.setName(showTime.getMovie().getName());
            ticket.setPrice(showTime.getTicketPrice());
            ticket.setTicketStatus(Ticket.TicketStatus.UNUSED);
            ticket.setTicketCode(generateTicketCode());
            tickets.add(ticket);
        }

        booking.setTickets(tickets);

        return bookingRepository.save(booking);
    }

    private String generateTicketCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public Map<String, Object> getTickUserByShowtime(Integer userId, Integer showtimeId) {
        Object[] result = bookingRepository.getTicketCountAndMaxTicketByUserAndShowtime(userId, showtimeId);
    
        Map<String, Object> response = new HashMap<>();
        
        
        if (result == null || result.length == 0) {
            response.put("ticketCount", 0);
            response.put("maxTicket", showTimeRepository.findById(showtimeId)
                .map(ShowTime::getMaxTicket)
                .orElse(0));
        } else {
            Object[] row = (Object[]) result[0];
            response.put("ticketCount", row[0]) ;
            response.put("maxTicket", row[1]);
        }
        
        return response;
    }

    public List<BookingDTO> getBookingDTOs(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Booking> bookings = bookingRepository.findByUser(user);
        List<BookingDTO> bookingDTOs = bookings.stream()
                .map(BookingDTO::new)
                .toList();
        return bookingDTOs;
    }

    public Booking getBookingById(Integer bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
    }

    public Booking updateBooking(Booking booking) {
        return bookingRepository.save(booking);
    }    
    
    public void deleteBooking(Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        bookingRepository.delete(booking);
    }

    public List<BookingDTO> findPaidBookingsWithUsedTickets(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Booking> bookings = bookingRepository.findPaidBookingsWithUsedTickets(user);
        return bookings.stream()
                .map(BookingDTO::new)
                .toList();
    }
    
}
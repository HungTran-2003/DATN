package haui.doan.ticket_booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import haui.doan.ticket_booking.DTO.BookingDTO;
import haui.doan.ticket_booking.model.*;
import haui.doan.ticket_booking.model.Booking.PaymentStatus;
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
        if (couponCode != null) {
            coupon = couponRepository.findByCode(couponCode)
                    .orElseThrow(() -> new RuntimeException("Coupon not found"));
            int numberTicketUsed = userCouponRepository.countCouponUsed(coupon);
            if (numberTicketUsed == coupon.getAmount()) {
                new RuntimeException("Vé đã được sử dụng hết");
            }
            if (coupon.getStatus() == Coupon.Status.INACTIVATE) {
                new RuntimeException("Vé này đã hết hạn");
            }
            UserCoupon userCoupon = new UserCoupon();
            userCoupon.setUser(user);
            userCoupon.setCoupon(coupon);
            userCoupon.setTimeUsed(LocalDateTime.now());    
            userCouponRepository.save(userCoupon);
            if (numberTicketUsed+1 == coupon.getAmount()) {
                coupon.setStatus(Coupon.Status.INACTIVATE);
                couponRepository.save(coupon);
            }
        }  

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
        bookings = sortBookings(bookings);
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
    
    @Transactional
    public void deleteBooking(Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        bookingRepository.delete(booking);
        System.out.println("đã xóa bookingId: " + bookingId);
    }

    public List<BookingDTO> findPaidBookingsWithUsedTickets(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Booking> bookings = bookingRepository.findPaidBookingsWithUsedTickets(user);
        return bookings.stream()
                .map(BookingDTO::new)
                .toList();
    }

    public List<BookingDTO> findBookingNotShowing(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Booking> bookings = bookingRepository.findBookingNotShowing(user);
        bookings = sortBookings(bookings);
        return bookings.stream()
                .map(BookingDTO::new)
                .toList();
    }

    @Transactional
    public void deteteBookings(List<Integer> ids){
        bookingRepository.deleteAllById(ids);
        System.out.println("đã xóa bookingId: " + ids.toString());
    }



    private List<Booking> sortBookings(List<Booking> bookings) {
        bookings.sort((b1, b2) -> {
        // So sánh theo trạng thái thanh toán
            boolean b1Paid = b1.getPaymentStatus() == Booking.PaymentStatus.PAID;
            boolean b2Paid = b2.getPaymentStatus() == Booking.PaymentStatus.PAID;
            if (b1Paid && !b2Paid) {
                return -1;
            } else if (!b1Paid && b2Paid) {
                return 1;
            } else {
                return b1.getShowTime().getStartTime().compareTo(b2.getShowTime().getStartTime());
            }
            });
        return bookings;
    }
    
    public List<Booking> findBookingPending(){
        return bookingRepository.findBookingPending(PaymentStatus.PENDING);
    }
}
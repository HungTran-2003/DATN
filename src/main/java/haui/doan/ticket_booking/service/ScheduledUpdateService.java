package haui.doan.ticket_booking.service;

import haui.doan.ticket_booking.model.ShowTime;
import haui.doan.ticket_booking.controller.user.BookingController;
import haui.doan.ticket_booking.model.Booking;
import haui.doan.ticket_booking.model.Coupon;
import haui.doan.ticket_booking.model.Movie;
import haui.doan.ticket_booking.model.Ticket;
import haui.doan.ticket_booking.repository.ShowTimeRepository;
import haui.doan.ticket_booking.repository.BookingRepository;
import haui.doan.ticket_booking.repository.CouponRepository;
import haui.doan.ticket_booking.repository.MovieRepository;
import haui.doan.ticket_booking.repository.TicketRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.annotation.PostConstruct;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

@Service
public class ScheduledUpdateService {

    @Autowired
    private ShowTimeRepository showTimeRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private TicketRepository ticketRepository;  
    
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingController bookingController;

    @Autowired
    private CouponRepository couponRepository;


    @PostConstruct
    @Transactional
    public void checkStatusOnStartup() {
        Date currentTime = new Date();
        
        // Update ticket statuses for expired showtimes
        List<Ticket> expiredTickets = ticketRepository.findExpiredUnusedTickets(currentTime);
        for (Ticket ticket : expiredTickets) {
            ticket.setTicketStatus(Ticket.TicketStatus.USED);
        }
        if (!expiredTickets.isEmpty()) {
            ticketRepository.saveAll(expiredTickets);
        }

        // Update movie statuses
        List<Movie> movies = movieRepository.findAllWithShowTimes();
        for (Movie movie : movies) {
            if (movie.getShowTimes() != null && !movie.getShowTimes().isEmpty()) {
                long totalShowTimes = movie.getShowTimes().size();
                long finishedShowTimes = movie.getShowTimes().stream()
                    .filter(st -> st.getStatus() == ShowTime.Status.FINISHED_SHOWING)
                    .count();

                if (totalShowTimes == finishedShowTimes) {
                    movie.setStatus(Movie.Status.FINISHED_SHOWING);
                    movieRepository.save(movie);
                }
            }
        }

        // Update Coupon Status
        List<Coupon> expiredCoupons = couponRepository.getCouponInactivated(currentTime);
        for(Coupon coupon : expiredCoupons){
            coupon.setStatus(Coupon.Status.INACTIVATE);
        }
        if (!expiredCoupons.isEmpty()) {
            couponRepository.saveAll(expiredCoupons);
        }

    }

    @Scheduled(fixedRate = 300000) // Runs every 5 minutes
    @Transactional
    public void updateStatuses() {
        Date currentTime = new Date();

        // Update ShowTime statuses
        List<ShowTime> showTimes = showTimeRepository.findAll();
        for (ShowTime showTime : showTimes) {
            // Update to NOW_SHOWING if current time is past start time
            if (showTime.getStatus() == ShowTime.Status.COMING_SOON 
                && currentTime.after(showTime.getStartTime())) {
                showTime.setStatus(ShowTime.Status.NOW_SHOWING);
                showTimeRepository.save(showTime);
            }
            
            // Update to FINISHED_SHOWING if current time is past end time
            if (showTime.getStatus() == ShowTime.Status.NOW_SHOWING 
                && currentTime.after(showTime.getEndTime())) {
                showTime.setStatus(ShowTime.Status.FINISHED_SHOWING);
                showTimeRepository.save(showTime);

                // Update tickets to USED when showtime ends
                List<Ticket> expiredTickets = ticketRepository.findExpiredUnusedTickets(currentTime);
                for (Ticket ticket : expiredTickets) {
                    ticket.setTicketStatus(Ticket.TicketStatus.USED);
                }
                if (!expiredTickets.isEmpty()) {
                    ticketRepository.saveAll(expiredTickets);
                }
                
                // Get associated movie and check all its showtimes
                Movie movie = showTime.getMovie();
                if (movie != null && movie.getShowTimes() != null && !movie.getShowTimes().isEmpty()) {
                    long totalShowTimes = movie.getShowTimes().size();
                    long finishedShowTimes = movie.getShowTimes().stream()
                        .filter(st -> st.getStatus() == ShowTime.Status.FINISHED_SHOWING)
                        .count();

                    // If all showtimes are finished, update movie status to FINISHED_SHOWING
                    if (totalShowTimes == finishedShowTimes) {
                        movie.setStatus(Movie.Status.FINISHED_SHOWING);
                        movieRepository.save(movie);
                    }
                }
            }
        }
    }

    @Scheduled(fixedRate = 330000)
    public void checkPaymentPendingTask(){
        final Map<Integer, LocalDateTime> bookingPending = bookingController.getBookingPending();
        if (bookingPending.isEmpty()) {
            System.out.println("empty");
            return;
        } 
        try {
            List<Integer> ids = new ArrayList<>();
            LocalDateTime current = LocalDateTime.now();
            System.out.println(current);
            LinkedHashMap<Integer, LocalDateTime> copy = new LinkedHashMap<>(bookingPending);
            for(Integer key : copy.keySet()){
                System.out.println(bookingPending.get(key));
                System.out.println(ChronoUnit.MINUTES.between(bookingPending.get(key), current));
                if (ChronoUnit.MINUTES.between(bookingPending.get(key), current) >= 5 ) {
                    ids.add(key);
                    System.out.println("sẽ xóa key " + key);
                    bookingPending.remove(key);
                } else {
                    break;
                }
            }
            if(!ids.isEmpty()){
                deteteBookings(ids);
                System.out.println("Removed: " + ids.toString());
            } 
        } catch (IllegalArgumentException e) {
            System.err.println("Lỗi khi lập lịch: " + e.getMessage());
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void updateCouponStatus() {
        Date currentTime = new Date();
        List<Coupon> expiredCoupons = couponRepository.getCouponInactivated(currentTime);
        for (Coupon coupon : expiredCoupons) {
            coupon.setStatus(Coupon.Status.INACTIVATE);
        }
        if (!expiredCoupons.isEmpty()) {
            couponRepository.saveAll(expiredCoupons);
        }
    }

    @Transactional
    public void deteteBookings(List<Integer> ids){
        bookingRepository.deleteAllById(ids);
        System.out.println("đã xóa bookingId: " + ids.toString());
    }


}
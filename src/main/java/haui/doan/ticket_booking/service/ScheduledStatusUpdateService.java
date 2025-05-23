package haui.doan.ticket_booking.service;

import haui.doan.ticket_booking.model.ShowTime;
import haui.doan.ticket_booking.model.Booking;
import haui.doan.ticket_booking.model.Movie;
import haui.doan.ticket_booking.model.Ticket;
import haui.doan.ticket_booking.repository.ShowTimeRepository;
import haui.doan.ticket_booking.repository.BookingRepository;
import haui.doan.ticket_booking.repository.MovieRepository;
import haui.doan.ticket_booking.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.annotation.PostConstruct;

import java.util.Date;
import java.util.List;

@Service
public class ScheduledStatusUpdateService {

    @Autowired
    private ShowTimeRepository showTimeRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private TicketRepository ticketRepository;  
    
    @Autowired
    private BookingRepository bookingRepository;

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
}
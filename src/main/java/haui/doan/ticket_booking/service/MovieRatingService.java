package haui.doan.ticket_booking.service;

import haui.doan.ticket_booking.DTO.MovieReviewDTO;
import haui.doan.ticket_booking.model.Movie;
import haui.doan.ticket_booking.model.MovieComment;
import haui.doan.ticket_booking.model.MovieRating;
import haui.doan.ticket_booking.model.User;
import haui.doan.ticket_booking.repository.MovieCommentRepository;
import haui.doan.ticket_booking.repository.MovieRatingRepository;
import haui.doan.ticket_booking.repository.MovieRepository;
import haui.doan.ticket_booking.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class MovieRatingService {
    private final MovieRatingRepository movieRatingRepository;
    private final MovieCommentRepository movieCommentRepository;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;

    @Transactional
    public MovieRating addRatingAndComment(MovieReviewDTO request) {
        // Get current user from security context
        User currentUser = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
        
        // Get movie
        Movie movie = movieRepository.findById(request.getMovieId())
            .orElseThrow(() -> new EntityNotFoundException("Movie not found"));

        // Create and save rating
        MovieRating rating = new MovieRating();
        rating.setUser(currentUser);
        rating.setMovie(movie);
        rating.setRatingValue(request.getRating());
        rating.setRatingDate(LocalDateTime.now());
        MovieRating savedRating = movieRatingRepository.save(rating);

        // If comment is provided, create and save comment
        if (StringUtils.hasText(request.getComment())) {
            MovieComment comment = new MovieComment();
            comment.setCommentText(request.getComment());
            comment.setMovie(movie);
            comment.setUser(currentUser);
            comment.setCommentDate(LocalDateTime.now());
            movieCommentRepository.save(comment);
        }
        return savedRating;
    }
}

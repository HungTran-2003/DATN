package haui.doan.ticket_booking.service;

import haui.doan.ticket_booking.DTO.MovieReviewDTO;
import haui.doan.ticket_booking.DTO.ReviewDetailDTO;
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

        User currentUser = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
        
        Movie movie = movieRepository.findById(request.getMovieId())
            .orElseThrow(() -> new EntityNotFoundException("Movie not found"));

        MovieRating rating = new MovieRating();
        rating.setUser(currentUser);
        rating.setMovie(movie);
        rating.setRatingValue(request.getRating());
        rating.setRatingDate(LocalDateTime.now());
        MovieRating savedRating = movieRatingRepository.save(rating);

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

    public ReviewDetailDTO getReview(Integer userId, Integer movieId){
        User currentUser = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
        
        Movie movie = movieRepository.findById(movieId)
            .orElseThrow(() -> new EntityNotFoundException("Movie not found"));

        return movieRatingRepository.getReviewDetail(currentUser, movie);

    }

    @Transactional
    public ReviewDetailDTO updateReview(ReviewDetailDTO review){
        ReviewDetailDTO newReview = new ReviewDetailDTO();
        MovieRating rating = movieRatingRepository.findById(review.getRattingId())
        .orElseThrow(() -> new EntityNotFoundException("Ratting not found"));
        
        MovieComment comment = null;
        if (review.getCommentId() != null) {
            comment = movieCommentRepository.findById(review.getCommentId())
            .orElseThrow(() -> new EntityNotFoundException("Comment not found"));
        } else if (!review.getComment().isEmpty()) {
            comment = new MovieComment();
            comment.setMovie(rating.getMovie());
            comment.setUser(rating.getUser());
        } 

        LocalDateTime currentDate = LocalDateTime.now();

        rating.setRatingValue(review.getRating());
        rating.setRatingDate(currentDate);
        rating = movieRatingRepository.save(rating);

        newReview.setRattingId(rating.getRatingId());
        newReview.setRating(rating.getRatingValue());

        if (comment!= null) {
            comment.setCommentText(review.getComment());
            comment.setCommentDate(currentDate);
            comment = movieCommentRepository.save(comment);
            newReview.setCommentId(comment.getCommentId());
            newReview.setComment(comment.getCommentText());
        }

        return newReview;
    }
}

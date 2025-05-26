package haui.doan.ticket_booking.controller;

import haui.doan.ticket_booking.DTO.MovieDTO;
import haui.doan.ticket_booking.model.Genre;
import haui.doan.ticket_booking.model.Movie.Status;
import haui.doan.ticket_booking.service.MovieService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;




@RestController
@RequestMapping("/api/movies")
public class MovieController {

    @Autowired
    private MovieService movieService;

    @PostMapping("/add")
    public ResponseEntity<String> addMovie(@RequestBody MovieDTO movieDTO){
        try{
            String result = movieService.addMovie(movieDTO);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(e.getMessage());
        }
    }

    @GetMapping("/getAll")
    public ResponseEntity<?> getSimpleMovies() {
        try {
            List<Map<String, Object>> movies = movieService.getAllSimpleMovies();
            return ResponseEntity.ok(movies);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchMovies(@RequestParam String searchText) {
        try {
            List<Map<String, Object>> movies = movieService.searchMovies(searchText);
            return ResponseEntity.ok(movies);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(e.getMessage());
        }
    }
    
    @GetMapping("/showing")
    public ResponseEntity<?> getMovieShowing() {
        try {
            List<Map<String, Object>> movies = movieService.getMovieByDate(Status.NOW_SHOWING);
            return ResponseEntity.ok(movies);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(e.getMessage());
        }
    }

    @GetMapping("/coming")
    public ResponseEntity<?> getMovieComing() {
        try {
            List<Map<String, Object>> movies = movieService.getMovieByDate(Status.COMING_SOON);
            return ResponseEntity.ok(movies);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(e.getMessage());
        }
    }

    @GetMapping("/special")
    public ResponseEntity<?> getMovieSpecial() {
        try {
            List<Map<String, Object>> movies = movieService.getMovieByDate(Status.SPECIAL);
            return ResponseEntity.ok(movies);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(e.getMessage());
        }
    }

    @GetMapping("/detail/{userId}/{movieId}")
    public ResponseEntity<?> getMoviesWithFavoriteCount(@PathVariable Integer userId, @PathVariable Integer movieId) {
        try {
            Optional<Map<String, Object>> movies = movieService.getMoviesDetail(movieId, userId);
            return ResponseEntity.ok(movies);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
        }
    }

    @PostMapping("update")
    public ResponseEntity<?> updateMovie(
        @RequestParam(value = "imagePoster", required = false) MultipartFile imagePoster,
        @RequestParam(value = "imageBanner", required = false) MultipartFile imageBanner,
        @RequestParam("movieId") String movieId,
        @RequestParam("type") String type,
        @RequestParam("status") String status) {
        try {
            String result = movieService.updateMovie(Integer.valueOf(movieId), type, status, imagePoster, imageBanner);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(e.getMessage());
        }
    }

    @GetMapping("/getAllGenre")
    public ResponseEntity<?> getAllGenre() {
        try {
            List<Genre> genres = movieService.getAllGenres();
            return ResponseEntity.ok(genres);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(e.getMessage());
        }
    }

    @GetMapping("/filter")
    public ResponseEntity<?> filterMovie(
        @RequestParam(value = "searchText") String searchText,
        @RequestParam(value = "year", required = false) String year,
        @RequestParam(value = "status", required = false) Status status,
        @RequestParam(value = "type", required = false) String type,
        @RequestParam(value = "genres", required = false) List<Integer> genres) {

        try {
            List<Map<String, Object>> movies = movieService.fliterMovie(searchText, year, status, type, genres);
            return ResponseEntity.ok(movies);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{movieId}")
    public ResponseEntity<?> deleteMovie(@PathVariable Integer movieId) {
        try {
            movieService.deleteMovie(movieId);
            return ResponseEntity.ok("Xoá phim thành công");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(e.getMessage());
        }
    }

    @GetMapping("/test/{movieId}")
    public Long testFavorite(@PathVariable Integer movieId) {
        return movieService.testFavorite(movieId);
    }

    @GetMapping("/getMovieFr/{userId}")
    public ResponseEntity<?> getMovieFr(@PathVariable Integer userId ) {
        try {
            List<Map<String, Object>> movies = movieService.getMovieFr(userId);
            return ResponseEntity.ok(movies);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(e.getMessage());
        }
    }
}
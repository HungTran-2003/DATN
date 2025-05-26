package haui.doan.ticket_booking.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.concurrent.CompletableFuture;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Optional;

import org.aspectj.apache.bcel.classfile.Module.Uses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import haui.doan.ticket_booking.DTO.MovieDTO;
import haui.doan.ticket_booking.model.Actor;
import haui.doan.ticket_booking.model.Director;
import haui.doan.ticket_booking.model.FavoriteMovie;
import haui.doan.ticket_booking.model.Genre;
import haui.doan.ticket_booking.model.Movie;
import haui.doan.ticket_booking.model.Movie.Status;
import haui.doan.ticket_booking.model.User;
import haui.doan.ticket_booking.repository.ActorRepository;
import haui.doan.ticket_booking.repository.DirectorRepository;
import haui.doan.ticket_booking.repository.FavoriteMovieRep;
import haui.doan.ticket_booking.repository.GenreRepository;
import haui.doan.ticket_booking.repository.MovieRepository;
import haui.doan.ticket_booking.repository.UserRepository;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import jakarta.transaction.Transactional;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final DirectorRepository directorRepository;
    private final ActorRepository actorRepository;
    private final GenreRepository genreRepository;
    private final ImageService imageService; 
    private final FavoriteMovieRep favoriteMovieRepository;
    private final UserRepository userRepository;

    @Autowired
    public MovieService(
            MovieRepository movieRepository,
            GenreRepository genreRepository,
            ActorRepository actorRepository,
            DirectorRepository directorRepository,
            ImageService imageService,
            FavoriteMovieRep favoriteMovieRepository,
            UserRepository userRepository
    ) {
        this.movieRepository = movieRepository;
        this.genreRepository = genreRepository;
        this.actorRepository = actorRepository;
        this.directorRepository = directorRepository;
        this.imageService = imageService; 
        this.favoriteMovieRepository = favoriteMovieRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public String addMovie(MovieDTO movieDTO) throws Exception {
        String validationError = validateMovieDTO(movieDTO);
        if (validationError != null) {
            throw new IllegalArgumentException(validationError);
        }

        CompletableFuture<String> posterFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return imageService.saveImage(movieDTO.getPoster_path(), "poster_" + movieDTO.getId() + ".jpg");
            } catch (Exception e) {
                throw new RuntimeException("Failed to save poster image", e);
            }
        });

        // Tải ảnh backdrop trong nền
        CompletableFuture<String> backdropFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return imageService.saveImage(movieDTO.getBackdrop_path(), "backdrop_" + movieDTO.getId() + ".jpg");
            } catch (Exception e) {
                throw new RuntimeException("Failed to save poster image", e);
            }  
        });

        // Chờ cả hai ảnh được tải xong
        CompletableFuture.allOf(posterFuture, backdropFuture).join();

        // Lấy kết quả từ các tác vụ bất đồng bộ
        String posterPath = posterFuture.get();
        String backdropPath = backdropFuture.get();

        // Nếu tải ảnh thành công, tiếp tục thêm Movie
        Movie movie = new Movie();
        movie.setTmdbId(movieDTO.getId());
        movie.setName(movieDTO.getTitle());
        movie.setDescription(movieDTO.getOverview());
        movie.setDuration(movieDTO.getDuration());
        movie.setReleaseDate((movieDTO.getRelease_date()));
        movie.setPosterUrl(posterPath); 
        movie.setBackdropPath(backdropPath); 
        movie.setLanguage(movieDTO.getLanguage());
        movie.setTmdb(movieDTO.getTmdbScore());
        movie.setStatus(Status.UNDETERMINED);
        movie.setActors(processActors(movieDTO));
        movie.setDirectors(processDirictor(movieDTO));
        movie.setGenres(processGenres(movieDTO));
        movie.setType(movieDTO.getType());

        return movieRepository.save(movie) != null? "Thêm phim thành công" : "Thêm phim thất bại";
    }

    public String validateMovieDTO(MovieDTO movieDTO) {
        if (movieDTO.getId() == null) return "Không lấy được ID phim";
        if (movieDTO.getTitle() == null || movieDTO.getTitle().isBlank()) return "Không lấy được tên phim";
        if (movieDTO.getOverview() == null || movieDTO.getOverview().isBlank()) return "Không lấy được mô tả phim";
        if (movieDTO.getBackdrop_path() == null || movieDTO.getBackdrop_path().isBlank()) return "Không lấy được ảnh backdrop";
        if (movieDTO.getPoster_path() == null || movieDTO.getPoster_path().isBlank()) return "Không lấy được ảnh poster";
        if (movieDTO.getRelease_date() == null || movieDTO.getRelease_date().isBlank()) return "không lấy được ngày phát hành";
        if (movieDTO.getGenres() == null || movieDTO.getGenres().isEmpty()) return "không lấy được thể loại phim";
        if (movieDTO.getDuration() == null || movieDTO.getDuration() <= 0) return "không lấy được thời gian phim";
        if (movieDTO.getLanguage() == null || movieDTO.getLanguage().isBlank()) return "không lấy được ngôn ngữ phim";
        if (movieDTO.getTmdbScore() == null) return "không lấy được điểm TMDB phim";
        if (movieDTO.getDirector() == null || movieDTO.getDirector().isEmpty()) return "không lấy được đạo diễn phim";
        if (movieDTO.getActors() == null || movieDTO.getActors().isEmpty()) return "không lấy được diễn viên phim";
        if (movieDTO.getType() == null) return "không lấy được loại phim";
    
        return null; // Hợp lệ
    }

    private List<Actor> processActors(MovieDTO movieDTO) {
        List<Integer> actorIds = movieDTO.getActors().stream()
                .map(Actor::getTmdbId)
                .collect(Collectors.toList());

        List<Actor> processActors = actorRepository.findBytmdbIdIn(Set.copyOf(actorIds));
        if(processActors.isEmpty()) {
            processActors = actorRepository.saveAll(movieDTO.getActors());
        } else{
            for (Actor actor : movieDTO.getActors()) {
                if (!processActors.contains(actor)) {
                    actor = actorRepository.save(actor);
                    processActors.add(actor);
                }
            }
        }
        return processActors;
    }

    private List<Director> processDirictor(MovieDTO movieDTO) {
        List<Integer> directorIds = movieDTO.getDirector().stream()
                .map(Director::getTmdbId)
                .collect(Collectors.toList());

        List<Director> processDirector = directorRepository.findBytmdbIdIn(Set.copyOf(directorIds));
        if(processDirector.isEmpty()) {
            processDirector = directorRepository.saveAll(movieDTO.getDirector());
        } else{
            for (Director director : movieDTO.getDirector()) {
                if (!processDirector.contains(director)) {
                    director = directorRepository.save(director);
                    processDirector.add(director);
                }
            }
        }
        return processDirector;
    }

    private List<Genre> processGenres(MovieDTO movieDTO) {
        List<String> names = movieDTO.getGenres().stream()
                .map(Genre::getName)
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        List<Genre> processGenres = genreRepository.findByNameIgnoreCaseIn(Set.copyOf(names));
        if(processGenres.isEmpty()) {
            processGenres = genreRepository.saveAll(movieDTO.getGenres());
        } else{
            for (Genre genre : movieDTO.getGenres()) {
                if (!processGenres.contains(genre)) {
                    genre = genreRepository.save(genre);
                    processGenres.add(genre);
                }
            }
        }
        return processGenres;
    }

    public List<Map<String, Object>> getAllSimpleMovies() {
        List<Movie> movies = movieRepository.findAll();
        movies = sortMovie(movies);
        return movies.stream()
            .map(movie -> {
                Map<String, Object> movieMap = new HashMap<>();
                movieMap.put("id", movie.getMovieId());
                movieMap.put("name", movie.getName());
                movieMap.put("posterUrl", movie.getPosterUrl());
                movieMap.put("duration", movie.getDuration());
                movieMap.put("status", movie.getStatus().name());
                movieMap.put("averageRating", movieRepository.findAverageRatingByMovieId(movie.getMovieId())
                    .orElse(BigDecimal.ZERO));
                return movieMap;
            })
            .collect(Collectors.toList());
    }

    private List<Movie> sortMovie(List<Movie> movies) {
        List<String> statusOrder = List.of("SPECIAL", "COMING_SOON", "UNDETERMINED", "NOW_SHOWING", "FINISHED_SHOWING");

        Map<String, Integer> priorityMap = new HashMap<>();
        for (int i = 0; i < statusOrder.size(); i++) {
            priorityMap.put(statusOrder.get(i), i);
        }

        movies.sort(Comparator.comparingInt(movie ->
            priorityMap.getOrDefault(movie.getStatus().toString(), Integer.MAX_VALUE)
        ));

        return movies;
    }
    
    public List<Map<String, Object>> searchMovies(String searchText) {
        List<Movie> movies = movieRepository.searchMovie(searchText);
        movies = sortMovie(movies);
        return movies.stream()
            .map(movie -> {
                Map<String, Object> movieMap = new HashMap<>();
                movieMap.put("id", movie.getMovieId());
                movieMap.put("name", movie.getName());
                movieMap.put("posterUrl", movie.getPosterUrl());
                movieMap.put("duration", movie.getDuration());
                movieMap.put("status", movie.getStatus().name());
                movieMap.put("averageRating", movieRepository.findAverageRatingByMovieId(movie.getMovieId())
                    .orElse(BigDecimal.ZERO));
                return movieMap;
            })
            .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getMovieByDate(Status status) {
        List<Object[]> results = movieRepository.getMovieByDate(status);
        List<Map<String, Object>> response = results.stream()
            .map(row -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", row[0]);
                map.put("posterUrl", row[1]);
                map.put("name", row[2]);
                map.put("duration", row[3]);
                if (row[4] != null) {
                    map.put("averageRating", row[4]);
                } else {
                    map.put("averageRating", 0);
                    
                }
                if (row[5] != null) {
                    map.put("ratingCount", row[5]);
                } else {
                    map.put("ratingCount", 0);
                    
                }
                return map;
            })
            .collect(Collectors.toList());
        return response;
    }

    public Optional<Map<String, Object>> getMoviesDetail(Integer movieId, Integer userId) {
        Optional<Movie> result = movieRepository.findById(movieId);
        List<Object[]> resultSt = movieRepository.getStatistics(movieId);
        List<Object[]> reviews =movieRepository.getReviewMovie(movieId);
        Boolean favortive = false;
        if (userId != 0) {
            FavoriteMovie.FavoriteMovieId favoriteId = new FavoriteMovie.FavoriteMovieId(userId, movieId);
            favortive = favoriteMovieRepository.existsById(favoriteId);
        }

        List<Map<String, Object>> reviewsMap = new ArrayList<>();
        for(Object[] review : reviews){
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("userId", review[0]);
            resultMap.put("name", review[1]);
            resultMap.put("ratingId", review[2]);
            resultMap.put("ratingValue", review[3]);
            resultMap.put("commentId", review[4]);
            resultMap.put("commentText", review[5]);
            reviewsMap.add(resultMap);
        }

        Map<String, Object> response = new HashMap<>();
        if (result.isPresent() ) {
            Movie movie = result.get();
            response.put("id", movie.getMovieId());
            response.put("name", movie.getName());
            response.put("posterUrl", movie.getPosterUrl());
            response.put("duration", movie.getDuration());
            response.put("description", movie.getDescription());
            response.put("releaseDate", movie.getReleaseDate());
            response.put("language", movie.getLanguage());
            response.put("backdropPath", movie.getBackdropPath());
            response.put("actors", movie.getActors());
            response.put("directors", movie.getDirectors());
            response.put("genres", movie.getGenres());
            response.put("type", movie.getType());
            response.put("status", movie.getStatus().name());
            response.put("totalFavorites", resultSt.get(0)[1] == null? 0 : resultSt.get(0)[1]);
            response.put("averageRating", resultSt.get(0)[0] == null? 0 : resultSt.get(0)[0]);
            response.put("favortive", favortive);
            response.put("reviews", reviewsMap);
        }
        return Optional.of(response);

    }
   
    @Transactional
    public String updateMovie(
        Integer movieId,
        String type,
        String status,
        MultipartFile imagePoster,
        MultipartFile imageBanner
    ) {
        Movie movie = movieRepository.findById(movieId).orElse(null);
        if (movie == null) {
            return "Không tìm thấy phim với ID: " + movieId;
        }
        if (type != null && !type.isEmpty()) {
            movie.setType(type);
        }
        if (status != null && !status.isEmpty() && !status.equals("null")) {
            try {
                Status statusEnum = Status.valueOf(status);
                movie.setStatus(statusEnum);
            } catch (IllegalArgumentException e) {
                return "Trạng thái không hợp lệ: " + status;
            }
        }

        if (imagePoster != null && !imagePoster.isEmpty()) {
            try {
                int movieIdTmdb = movie.getTmdbId() != null ? movie.getTmdbId() : 0;
                String posterPath = "";
                String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                if (movieIdTmdb == 0) {
                    posterPath = imageService.saveFile(imagePoster, "poster_" + movieId + "_" + timestamp + ".jpg");
                } else {
                    posterPath = imageService.saveFile(imagePoster, "poster_" + movieIdTmdb + "_" + timestamp +  ".jpg");
                    System.out.println("posterPath: " + posterPath);
                }
                movie.setPosterUrl(posterPath);
            } catch (Exception e) {
                return "Lỗi khi tải ảnh poster: " + e.getMessage();
            }
        }
        if (imageBanner != null && !imageBanner.isEmpty()) {
            try {
                int movieIdTmdb = movie.getTmdbId() != null ? movie.getTmdbId() : 0;
                String backdropPath = "";
                String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                if (movieIdTmdb == 0) {
                    backdropPath = imageService.saveFile(imageBanner, "backdrop_"  + movieId + "_" + timestamp + ".jpg");
                } else {
                    backdropPath = imageService.saveFile(imageBanner, "backdrop_" + movieIdTmdb + "_" + timestamp + ".jpg");
                    System.out.println("backdropPath: " + backdropPath);
                }
                movie.setBackdropPath(backdropPath);
            } catch (Exception e) {
                return "Lỗi khi tải ảnh banner: " + e.getMessage();
            }
        }
        movieRepository.save(movie);
        return "Cập nhật phim thành công";
    }

    @Transactional
    public String deleteMovie(Integer movieId) {
        Movie movie = movieRepository.findById(movieId)
            .orElseThrow(() -> new IllegalArgumentException("Movie not found with id: " + movieId));
            
        // Delete associated image files
        if (movie.getPosterUrl() != null) {
            imageService.deleteImage(movie.getPosterUrl());
        }
        if (movie.getBackdropPath() != null) {
            imageService.deleteImage(movie.getBackdropPath());
        }
        
        movieRepository.delete(movie);
        return "Movie deleted successfully";
    }

    public List<Genre> getAllGenres() {
        return genreRepository.findAll();
    }

    public List<Map<String, Object>> fliterMovie(String searchText, String year, Status status,
        String type, List<Integer> genreIds) {
        
        List<Movie> movies = movieRepository.fliterMovie(searchText, year, status, type, genreIds);
        List<Map<String, Object>> movieList = movies.stream()
            .map(movie -> {
                Map<String, Object> movieMap = new HashMap<>();
                movieMap.put("id", movie.getMovieId());
                movieMap.put("name", movie.getName());
                movieMap.put("posterUrl", movie.getPosterUrl());
                movieMap.put("duration", movie.getDuration());
                movieMap.put("status", movie.getStatus().name());
                movieMap.put("averageRating", movieRepository.findAverageRatingByMovieId(movie.getMovieId())
                    .orElse(BigDecimal.ZERO));
                return movieMap;
            })
            .collect(Collectors.toList());   
        return movieList;     
    }

    public Long testFavorite(Integer movieId) {
        return movieRepository.testFavorite(movieId);
    }

    public List<Map<String, Object>> getMovieFr(Integer userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        List<FavoriteMovie> f = movieRepository.getMovieFavortive(user);
        List<Movie> movies = f.stream()
                                   .map(FavoriteMovie::getMovie)
                                   .collect(Collectors.toList());
        return movies.stream()
            .map(movie -> {
                System.out.println(movie.getMovieId());
                Map<String, Object> movieMap = new HashMap<>();
                movieMap.put("id", movie.getMovieId());
                movieMap.put("name", movie.getName());
                movieMap.put("posterUrl", movie.getPosterUrl());
                movieMap.put("duration", movie.getDuration());
                movieMap.put("status", movie.getStatus().name());
                movieMap.put("averageRating", movieRepository.findAverageRatingByMovieId(movie.getMovieId())
                    .orElse(BigDecimal.ZERO));
                return movieMap;
            })
            .collect(Collectors.toList());
    }
}



package haui.doan.ticket_booking.service.user;

import java.sql.Timestamp;
import java.util.Map;
import java.util.Optional;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import haui.doan.ticket_booking.model.User;
import haui.doan.ticket_booking.model.Movie;
import haui.doan.ticket_booking.DTO.UserDTO;
import haui.doan.ticket_booking.model.FavoriteMovie;
import haui.doan.ticket_booking.repository.UserRepository;
import haui.doan.ticket_booking.repository.MovieRepository;
import haui.doan.ticket_booking.repository.FavoriteMovieRep;
import haui.doan.ticket_booking.util.JwtUtil;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private FavoriteMovieRep favoriteMovieRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User addUser(Map<String, String> body) {
        User user = new User();
        user.setName(body.get("username"));
        user.setEmail(body.get("email"));
        user.setPasswordHash(passwordEncoder.encode(body.get("password")));
        user.setRegistrationDate(java.time.LocalDateTime.now());
        user.setRole(User.Role.USER); 
        user.setAccountStatus(User.AccountStatus.ACTIVE);// Chưa mã hóa mật khẩu
        return userRepository.save(user);
    }

    public Map<String, String> login(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(password, user.getPasswordHash())) {
                // Tạo token JWT
                String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
                return Map.of(
                    "token", token,
                    "role", user.getRole().name(),
                    "username", user.getName(),
                    "userId", user.getUserId().toString()
                );}
        }
        throw new RuntimeException("Invalid email or password");
    }

    @Transactional
    public String addToFavorites(Integer userId, Integer movieId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
            
        Movie movie = movieRepository.findById(movieId)
            .orElseThrow(() -> new RuntimeException("Movie not found"));

        FavoriteMovie.FavoriteMovieId favoriteId = new FavoriteMovie.FavoriteMovieId(userId, movieId);
        
        if (favoriteMovieRepository.existsById(favoriteId)) {
            throw new RuntimeException("Movie already in favorites");
        }

        FavoriteMovie favoriteMovie = new FavoriteMovie();
        favoriteMovie.setId(favoriteId);
        favoriteMovie.setUser(user);
        favoriteMovie.setMovie(movie);
        favoriteMovie.setAddedDate(new Timestamp(System.currentTimeMillis()));

        favoriteMovieRepository.save(favoriteMovie);
        
        return "Đã thêm vào danh sách yêu thích";
    }

    @Transactional
    public String removeFromFavorites(Integer userId, Integer movieId) {
        FavoriteMovie.FavoriteMovieId favoriteId = new FavoriteMovie.FavoriteMovieId(userId, movieId);
        
        if (!favoriteMovieRepository.existsById(favoriteId)) {
            throw new RuntimeException("Movie not found in favorites");
        }

        favoriteMovieRepository.deleteById(favoriteId);
        
        return "Đã xoá khỏi danh sách yêu thích";
    }

    public List<UserDTO> getAllUserRole() {
        return userRepository.findAllUserRole().stream()
            .map(UserDTO::new)
            .toList();
    }

    public User changePassword(Integer userId, String newPassword) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }

    public Boolean comfirmPassword(Integer userId, String password){
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        if (passwordEncoder.matches(password, user.getPasswordHash())) {
            return true;
        } 
        return false;
    }

    public User getUserbyEmail(String email){
        return userRepository.findByEmail(email).get();
    }

    public UserDTO getProfile(Integer userId){
        return new UserDTO(userRepository.findById(userId).get());
    }

    public User updateProfile(UserDTO userDTO){
        User user = userRepository.findById(userDTO.getUserId())
        .orElseThrow(() -> new RuntimeException("User not found"));

        user.setEmail(userDTO.getEmail());
        user.setPhoneNumber(userDTO.getPhoneNumber());

        return userRepository.save(user);
    }
}
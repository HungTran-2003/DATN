package haui.doan.ticket_booking.controller.user;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import haui.doan.ticket_booking.DTO.MovieReviewDTO;
import haui.doan.ticket_booking.DTO.ReviewDetailDTO;
import haui.doan.ticket_booking.DTO.UserDTO;
import haui.doan.ticket_booking.model.MovieRating;
import haui.doan.ticket_booking.model.User;
import haui.doan.ticket_booking.service.EmailService;
import haui.doan.ticket_booking.service.MovieRatingService;
import haui.doan.ticket_booking.service.user.UserService;





@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private MovieRatingService movieRatingService;

    @Autowired
    private EmailService emailService;

    @PostMapping("/add")
    public ResponseEntity<String> addUser(@RequestBody Map<String, String> body) {
        User createdUser = userService.addUser(body);
        return ResponseEntity.ok("Tạo tài khoản thành công");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        try{
            String email = body.get("email");
            String password = body.get("password");
            return ResponseEntity.ok(userService.login(email, password));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("Lỗi", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("Lỗi", e.getMessage()));
        }
    }

    @PostMapping("/favorite-movie")
    public ResponseEntity<?> addFavoriteMovie(@RequestBody Map<String, Integer> body) {
        try {
            String reponsi = userService.addToFavorites(body.get("userId"), body.get("movieId"));
            return ResponseEntity.ok(reponsi);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(e.getMessage());
        }
    }

    @DeleteMapping("/favorite-movie")
    public ResponseEntity<?> removeFavoriteMovie(@RequestParam("userId") Integer userId,
                                         @RequestParam("movieId") Integer movieId) {
        try {
            String response = userService.removeFromFavorites(userId, movieId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("Lỗi", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("Lỗi", e.getMessage()));
        }
    }
    
    @PostMapping("/review")
    public ResponseEntity<?> addReview(@RequestBody MovieReviewDTO body) {
        try {
            MovieRating response = movieRatingService.addRatingAndComment(body);
            if (response == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Thêm đánh giá thất bại");
            }
            return ResponseEntity.ok("Thêm đánh giá thành công");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("Lỗi: ", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("Lỗi", e.getMessage()));
        }

    }

    @GetMapping("/getAll")
    public ResponseEntity<?> getAll() {
        try {
            List<UserDTO> users = userService.getAllUserRole();
            return ResponseEntity.ok(users);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("Lỗi", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("Lỗi", e.getMessage()));
        }
    }
    
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestParam("userId") Integer userId,
                                            @RequestParam("newPassword") String newPassword) {
        try {
            User user = userService.changePassword(userId, newPassword);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Thay đổi mật khẩu thất bại");
            }
            return ResponseEntity.ok("Thay đổi mật khẩu thành công");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("Lỗi", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("Lỗi", e.getMessage()));
        }
    }

    @GetMapping("/comfirm-password")
    public ResponseEntity<?> comfirmPassword(@RequestParam("userId") Integer userId,
                                            @RequestParam("password") String password) {
        try {
        
            Boolean comfirm = userService.comfirmPassword(userId, password);
            if (!comfirm) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Mật khẩu không đúng");
            }
            return ResponseEntity.ok("Xác nhận thành công");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("Lỗi", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("Lỗi", e.getMessage()));
        }
    }

    @GetMapping("/email")
    public Integer getIdByEmail(@RequestParam String email) {
        return userService.getUserbyEmail(email).getUserId();
    }
    
    @GetMapping("/profile")
    public ResponseEntity<?> getProfileUser(@RequestParam Integer userId) {
        try {
            UserDTO reponsi = userService.getProfile(userId);
            if (reponsi == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Không tim thấy tài khoản");
            }
            return ResponseEntity.ok(reponsi);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("Lỗi", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("Lỗi", e.getMessage()));
        }
    }

    @PostMapping("/update-profile")
    public ResponseEntity<?> updateProfile(@RequestBody UserDTO userDTO) {
        try {
            User reponsi = userService.updateProfile(userDTO);
            if (reponsi == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Cập nhật tài khoản không thành công");
            }
            return ResponseEntity.ok("Cập nhật tài khoản thành công");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("Lỗi", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("Lỗi", e.getMessage()));
        }
    }
    
    @GetMapping("/getReview")
    public ResponseEntity<?> getReviewDetail(@RequestParam("userId") Integer Integer,
                                            @RequestParam("movieId") Integer movieId) {
        try {
            ReviewDetailDTO review = movieRatingService.getReview(Integer, movieId);
            if (review == null) {
                review = new ReviewDetailDTO();
            }
            return ResponseEntity.ok(review);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("Lỗi", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("Lỗi", e.getMessage()));
        }
    }

    @PatchMapping("/update-review")
    public ResponseEntity<?> updateReview(@RequestBody ReviewDetailDTO reviewDTO){
        try {
            ReviewDetailDTO review = movieRatingService.updateReview(reviewDTO);
            return ResponseEntity.ok(review);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("Lỗi", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("Lỗi", e.getMessage()));
        }
    }
    
    
    
}
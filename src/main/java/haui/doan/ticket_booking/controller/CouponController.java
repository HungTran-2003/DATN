package haui.doan.ticket_booking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import haui.doan.ticket_booking.DTO.CouponDTO;
import haui.doan.ticket_booking.service.CouponService;

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
public class CouponController {

    @Autowired
    private CouponService couponService;

    @PostMapping("/create")
    public ResponseEntity<?> createCoupon(@RequestBody CouponDTO request) {
        try {
            String result = couponService.createCoupon(request);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error creating coupon: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{couponId}")
    public ResponseEntity<?> deleteCoupon(@PathVariable Integer couponId) {
        try {
            String result = couponService.deleteCoupon(couponId);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi khi xóa mã giảm giá: " + e.getMessage());
        }
    }   
    @GetMapping("/getAll")
    public ResponseEntity<?> getAllCoupons() {
        try {
            List<CouponDTO> coupons = couponService.getAllCoupons();
            return ResponseEntity.ok(coupons);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi khi lấy danh sách mã giảm giá: " + e.getMessage());
        }
    }

    @GetMapping("/available/{userId}")
    public ResponseEntity<?> getAvailableCouponsForUser(@PathVariable Integer userId) {
        try {
            List<CouponDTO> coupons = couponService.getAvailableCouponsForUser(userId);
            return ResponseEntity.ok(coupons);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Lỗi khi lấy danh sách mã giảm giá khả dụng: " + e.getMessage());
        }
    }
}

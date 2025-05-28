package haui.doan.ticket_booking.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import haui.doan.ticket_booking.DTO.CinemaDTO;
import haui.doan.ticket_booking.DTO.CouponDTO;
import haui.doan.ticket_booking.model.Cinema;
import haui.doan.ticket_booking.model.Coupon;
import haui.doan.ticket_booking.repository.CinemaRepository;
import haui.doan.ticket_booking.repository.CouponRepository;

@Service
public class CouponService {

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CinemaRepository cinemaRepository;

    @Transactional
    public String createCoupon(CouponDTO request) {
        // Validate input
        if (request.getDiscount() == null || request.getDiscount() <= 0 || request.getDiscount() > 100) {
            throw new IllegalArgumentException("Discount must be between 0 and 100");
        }
        if (request.getExpirationDate() == null) {
            throw new IllegalArgumentException("Expiration date is required");
        }
        if (request.getCinemaIds() == null || request.getCinemaIds().isEmpty()) {
            throw new IllegalArgumentException("At least one cinema must be selected");
        }

        // Get cinemas
        List<Cinema> cinemas = cinemaRepository.findAllById(request.getCinemaIds());
        if (cinemas.size() != request.getCinemaIds().size()) {
            throw new IllegalArgumentException("One or more cinema IDs are invalid");
        }

        // Generate unique code
        String code = generateUniqueCode();
        while (couponRepository.existsByCode(code)) {
            code = generateUniqueCode();
        }

        // Create coupon
        Coupon coupon = new Coupon();
        coupon.setName(request.getName());
        coupon.setDiscount(request.getDiscount());
        coupon.setExpirationDate(request.getExpirationDate());
        coupon.setDescription(request.getDescription());
        coupon.setCode(code);
        coupon.setStatus(Coupon.Status.ACTIVATE);
        coupon.setCinemas(cinemas);
        coupon.setAmount(request.getAmount());

        couponRepository.save(coupon);
        return "Thêm mã giảm giá thành công";
    }

    @Transactional
    public String deleteCoupon(Integer couponId) {
        Coupon coupon = couponRepository.findById(couponId)
            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy mã giảm giá với ID: " + couponId));
        
        // Kiểm tra xem coupon đã được sử dụng chưa
        if (!coupon.getUserCoupons().isEmpty()) {
            throw new IllegalStateException("Không thể xóa mã giảm giá đã được sử dụng");
        }
        
        couponRepository.delete(coupon);
        return "Xóa mã giảm giá thành công";
    
    
    }      
    public List<CouponDTO> getAllCoupons() {
        List<Map<String, Object>> couponsWithCount = couponRepository.findAllWithUsedCount();
        
        List<CouponDTO> dtos = couponsWithCount.stream()
            .map(map -> {
                Coupon coupon = (Coupon) map.get("coupon");
                Long usedCount = (Long) map.get("usedCount");
                return convertToDTO(coupon, usedCount.intValue());
            })
            .collect(Collectors.toList()); 
        return sortCoupons(dtos);
    }

    public List<CouponDTO> getAvailableCouponsForUser(Integer userId) {
        List<Map<String, Object>> couponsWithCount = couponRepository.findAvailableCouponsForUser(userId);
        
        List<CouponDTO> dtos = couponsWithCount.stream()
            .map(map -> {
                Coupon coupon = (Coupon) map.get("coupon");
                Long usedCount = (Long) map.get("usedCount");
                return convertToDTO(coupon, usedCount.intValue());
            })
            .collect(Collectors.toList());
            
        return sortCoupons(dtos);
    }

    private CouponDTO convertToDTO(Coupon coupon, int usedCount) {
        CouponDTO dto = new CouponDTO();
        dto.setCouponId(coupon.getCouponId());
        dto.setName(coupon.getName());
        dto.setDiscount(coupon.getDiscount());
        dto.setExpirationDate(coupon.getExpirationDate());
        dto.setDescription(coupon.getDescription());
        dto.setCinemaIds(coupon.getCinemas().stream()
            .map(Cinema::getCinemaId)
            .collect(Collectors.toList()));

        dto.setCinemas(coupon.getCinemas().stream()
            .map(CinemaDTO::new)
            .collect(Collectors.toList()));

        dto.setCode(coupon.getCode());
        dto.setAmount(coupon.getAmount());
        dto.setStatus(coupon.getStatus().name());
        dto.setTotalUsed(usedCount);
        
        return dto;
    }
    
    private List<CouponDTO> sortCoupons(List<CouponDTO> coupons) {
        return coupons.stream()
            .sorted((c1, c2) -> {
                // Sắp xếp theo Status (ACTIVE trước)
                Coupon coupon1 = couponRepository.findByName(c1.getName()).orElse(null);
                Coupon coupon2 = couponRepository.findByName(c2.getName()).orElse(null);
                
                if (coupon1 != null && coupon2 != null && 
                    coupon1.getStatus() != coupon2.getStatus()) {
                    return coupon1.getStatus().compareTo(coupon2.getStatus());
                }
                // Sau đó sắp xếp theo thời gian (gần nhất lên trước)
                return c2.getExpirationDate().compareTo(c1.getExpirationDate());
            })
            .collect(Collectors.toList());
    }

    private String generateUniqueCode() {
        // Generate a code with format: COUP-XXXX-XXXX where X is alphanumeric
        String uuid = UUID.randomUUID().toString().toUpperCase().replace("-", "");
        return "COUP-" + uuid.substring(0, 4) + "-" + uuid.substring(4, 8);
    }

    public String updateCoupon(CouponDTO request){
        Coupon coupon = couponRepository.findById(request.getCouponId())
            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy mã giảm giá với ID: " + request.getCouponId()));

        List<Cinema> cinemas = cinemaRepository.findAllById(request.getCinemaIds());
        if (cinemas.size() != request.getCinemaIds().size()) {
            throw new IllegalArgumentException("One or more cinema IDs are invalid");
        }    
        
        coupon.setName(request.getName());
        coupon.setExpirationDate(request.getExpirationDate());
        coupon.setDescription(request.getDescription());
        coupon.setAmount(request.getAmount());
        coupon.setStatus(Coupon.Status.ACTIVATE);
        coupon = couponRepository.save(coupon);
        return "Cập nhật mã giảm giá " + coupon.getName() + " thành công";
    } 
}

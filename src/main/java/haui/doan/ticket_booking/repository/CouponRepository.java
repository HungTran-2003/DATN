package haui.doan.ticket_booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import haui.doan.ticket_booking.model.Coupon;
import haui.doan.ticket_booking.model.User;

import java.util.Optional;
import java.util.List;
import java.util.Map;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Integer> {
    boolean existsByCode(String code);
    Optional<Coupon> findByName(String name);

    @Query("SELECT COUNT(uc) FROM UserCoupon uc WHERE uc.coupon.couponId = :couponId")
    int countUsedCoupons(@Param("couponId") Integer couponId);

    @Query("SELECT new map(c as coupon, COUNT(uc) as usedCount) " +
           "FROM Coupon c " +
           "LEFT JOIN c.userCoupons uc " +
           "GROUP BY c")
    List<Map<String, Object>> findAllWithUsedCount();  

    @Query("SELECT new map(c as coupon, " +
           "(SELECT COUNT(uc1) FROM UserCoupon uc1 WHERE uc1.coupon = c) as usedCount) " +
           "FROM Coupon c " +
           "WHERE c.status = 'ACTIVATE' " +
           "AND c.expirationDate >= CURRENT_DATE " +
           "AND (NOT EXISTS (" +
           "    SELECT 1 FROM UserCoupon uc2 " +
           "    WHERE uc2.coupon = c " +
           "    AND uc2.user.userId = :userId" +
           "))")
    List<Map<String, Object>> findAvailableCouponsForUser(@Param("userId") Integer userId);

    Optional<Coupon> findByCode(String couponCode);
}

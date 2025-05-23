package haui.doan.ticket_booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import haui.doan.ticket_booking.model.Coupon;
import haui.doan.ticket_booking.model.UserCoupon;

@Repository
public interface UserCouponRepository extends JpaRepository<UserCoupon, Integer> {

    @Query("SELECT COUNT(uc.id) " +
        ("FROM UserCoupon uc WHERE uc.coupon =: coupon"))
    Integer countCouponUsed(@Param("coupon") Coupon coupon);
}

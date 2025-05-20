package haui.doan.ticket_booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import haui.doan.ticket_booking.model.UserCoupon;

@Repository
public interface UserCouponRepository extends JpaRepository<UserCoupon, Integer> {

}

package haui.doan.ticket_booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import haui.doan.ticket_booking.model.City;

public interface CityRepository extends JpaRepository<City, Integer> {
    // Có thể thêm các phương thức tùy chỉnh ở đây nếu cần
}
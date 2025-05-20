package haui.doan.ticket_booking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import haui.doan.ticket_booking.model.Cinema;

public interface CinemaRepository extends JpaRepository<Cinema, Integer> {
    
    @Query(value = "SELECT c.cinema_id, c.name FROM cinema c WHERE c.city_id = :cityId", nativeQuery = true)
    List<Object[]> findCinemasByCityIdNative(@Param("cityId") Integer cityId);

    @Query(value = "SELECT c.cinema_id, c.name FROM cinema c", nativeQuery = true)
    List<Object[]> getAllCinema();

}
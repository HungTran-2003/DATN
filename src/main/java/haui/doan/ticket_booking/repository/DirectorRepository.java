package haui.doan.ticket_booking.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import haui.doan.ticket_booking.model.Director;

@Repository
public interface DirectorRepository extends JpaRepository<Director, Integer> {
    List<Director> findBytmdbIdIn(Set<Integer> directorIds);
}

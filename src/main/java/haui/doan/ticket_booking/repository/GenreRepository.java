package haui.doan.ticket_booking.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import haui.doan.ticket_booking.model.Genre;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Integer> {

    @Query("SELECT g FROM Genre g WHERE LOWER(g.name) IN :names")
    List<Genre> findByNameIgnoreCaseIn(@Param("names") Set<String> lowerCaseNames);
}

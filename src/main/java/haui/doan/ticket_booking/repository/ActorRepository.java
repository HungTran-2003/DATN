package haui.doan.ticket_booking.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import haui.doan.ticket_booking.model.Actor;

@Repository
public interface ActorRepository extends JpaRepository<Actor, Integer> {
    List<Actor> findBytmdbIdIn(Set<Integer> actorIds);
}


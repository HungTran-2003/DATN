package haui.doan.ticket_booking.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import haui.doan.ticket_booking.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);
    
    @Query("SELECT u FROM User u WHERE u.role = 'USER'")
    List<User> findAllUserRole();
}

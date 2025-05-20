package haui.doan.ticket_booking.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "actor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Actor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "actor_id")
    private Integer actorId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "tmdb_id")
    @JsonProperty("id")
    private Integer tmdbId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Actor actor)) return false;
        return tmdbId.equals(actor.tmdbId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tmdbId);
    }
}

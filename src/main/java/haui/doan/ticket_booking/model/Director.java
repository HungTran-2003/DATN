package haui.doan.ticket_booking.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

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
@Table(name = "director")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Director {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "director_id")
    private Integer directorId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "tmdb_id")
    @JsonProperty("id")
    private Integer tmdbId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Director director)) return false;

        if (this.name == null && director.name == null) return true;
        if (this.name == null || director.name == null) return false;

        return this.name.equalsIgnoreCase(director.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tmdbId);
    }

}

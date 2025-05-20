package haui.doan.ticket_booking.DTO;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import haui.doan.ticket_booking.model.Actor;
import haui.doan.ticket_booking.model.Director;
import haui.doan.ticket_booking.model.Genre;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MovieDTO {
    private Integer id;
    private String title;
    private String overview;
    private String backdrop_path;
    private String poster_path;
    private String release_date;
    private List<Genre> genres;
    private List<Actor> actors;
    @JsonProperty("runtime")
    private Integer duration;
    @JsonProperty("original_language")
    private String language;
    @JsonProperty("vote_average")
    private Double tmdbScore;
    private List<Director> director;
    private String type;
    private String status;
}

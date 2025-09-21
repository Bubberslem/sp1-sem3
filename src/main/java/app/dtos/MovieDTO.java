package app.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieDTO {
    private long id;
    private String title;
    private String release_date;
    private String overview;
    private double popularity;
    private double vote_average;
    private List<Integer> genre_ids;
}

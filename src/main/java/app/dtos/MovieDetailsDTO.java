package app.dtos;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieDetailsDTO {
    private long id;
    private String title;
    private String release_date;
    private double popularity;
    private double vote_average;
    private List<GenreDTO> genres;
    private CreditsDTO credits;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GenreDTO {
        private long id;
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreditsDTO {
        private List<CastDTO> cast;
        private List<CrewDTO> crew;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CastDTO {
        private long id;
        private String name;
        private Integer order;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CrewDTO {
        private long id;
        private String name;
        private String job;
    }
}

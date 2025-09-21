package app.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreditsDTO {
    private long id;                 // movie id
    private List<CastDTO> cast;
    private List<CrewDTO> crew;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CastDTO {
        private long id;             // person TMDb id
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CrewDTO {
        private long id;             // person TMDb id
        private String name;
        private String job;          // fx "Director", "Writer"
    }
}

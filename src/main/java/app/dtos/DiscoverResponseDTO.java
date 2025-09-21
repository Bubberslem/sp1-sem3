package app.dtos;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter

public class DiscoverResponseDTO {
    private int page;
    private List<MovieDTO> results;
    private int total_pages;
    private int total_results;
}

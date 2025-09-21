package app.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "person", indexes = @Index(name = "ux_person_tmdb", columnList = "tmdbId", unique = true))
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private Long tmdbId;
    @Column(nullable = false)
    private String name;
}

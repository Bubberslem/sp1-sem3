package app.services;

import app.dtos.CreditsDTO;
import app.dtos.DiscoverResponseDTO;
import app.dtos.MovieDTO;
import app.entities.Genre;
import app.entities.Movie;
import app.entities.Person;
import app.config.HibernateConfig;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;

public class MovieServices {

    public List<MovieDTO> getAllMovies() {
        String apiKey = System.getenv("api_key"); // husk at sætte env var!
        String base = "https://api.themoviedb.org/3/discover/movie";
        String common = "&include_adult=false&include_video=false"
                + "&primary_release_date.gte=2020-01-01"
                + "&primary_release_date.lte=2025-09-17"
                + "&with_origin_country=DK"
                + "&with_original_language=da"
                + "&sort_by=vote_average.desc";

        FetchTools ft = new FetchTools();

        // 1) Første side – få total_pages
        String url1 = base + "?api_key=" + apiKey + "&page=1" + common;
        DiscoverResponseDTO first = ft.getFromApi(url1, DiscoverResponseDTO.class);

        List<MovieDTO> all = new ArrayList<>(first.getResults());
        int totalPages = Math.min(first.getTotal_pages(), 500); // maks 500 sider

        System.out.printf("Side 1/%d hentet, %d film%n", totalPages, all.size());

        // 2) Loop resten af siderne
        for (int page = 2; page <= totalPages; page++) {
            String url = base + "?api_key=" + apiKey + "&page=" + page + common;
            try {
                DiscoverResponseDTO resp = ft.getFromApi(url, DiscoverResponseDTO.class);
                if (resp != null && resp.getResults() != null) {
                    all.addAll(resp.getResults());
                    System.out.printf("Side %d/%d hentet, total=%d%n", page, totalPages, all.size());
                }
            } catch (RuntimeException e) {
                System.err.println("Fejl på side " + page + ": " + e.getMessage());
            }
        }

        System.out.println("Færdig. Antal film: " + all.size());

        // Store movies and their actors/directors in the database
        storeMoviesAndCredits(all);

        return all; // ✅ returnér listen
    }

    public CreditsDTO getMovieCredits(long movieId) {
        String apiKey = System.getenv("api_key");
        String url = "https://api.themoviedb.org/3/movie/" + movieId + "/credits?api_key=" + apiKey;
        FetchTools ft = new FetchTools();
        return ft.getFromApi(url, CreditsDTO.class);
    }

    // DTOs for genre API response
    private static class GenreListResponse {
        public List<GenreDTO> genres;
    }
    private static class GenreDTO {
        public Long id;
        public String name;
    }

    private Map<Long, String> fetchGenreNames() {
        String apiKey = System.getenv("api_key");
        String url = "https://api.themoviedb.org/3/genre/movie/list?api_key=" + apiKey + "&language=en";
        FetchTools ft = new FetchTools();
        GenreListResponse response = ft.getFromApi(url, GenreListResponse.class);
        Map<Long, String> genreMap = new HashMap<>();
        if (response != null && response.genres != null) {
            for (GenreDTO g : response.genres) {
                genreMap.put(g.id, g.name);
            }
        }
        return genreMap;
    }

    private void storeMoviesAndCredits(List<MovieDTO> movies) {
        Map<Long, String> genreNameCache = fetchGenreNames();

        EntityManager em = HibernateConfig.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try {
            for (MovieDTO movieDTO : movies) {
                // Check if movie already exists
                Movie movie = em.createQuery("SELECT m FROM Movie m WHERE m.tmdbId = :tmdbId", Movie.class)
                        .setParameter("tmdbId", movieDTO.getId())
                        .setMaxResults(1)
                        .getResultList()
                        .stream()
                        .findFirst()
                        .orElse(null);

                if (movie == null) {
                    movie = Movie.builder()
                            .tmdbId(movieDTO.getId())
                            .title(movieDTO.getTitle())
                            .releaseDate(movieDTO.getRelease_date())
                            .build();
                }

                // Handle genres
                Set<Genre> genres = new HashSet<>();
                if (movieDTO.getGenre_ids() != null) {
                    for (Integer genreId : movieDTO.getGenre_ids()) {
                        Genre genre = em.find(Genre.class, genreId.longValue());
                        if (genre == null) {
                            String genreName = genreNameCache.getOrDefault(genreId.longValue(), "Unknown");
                            // Check if a genre with this name already exists
                            List<Genre> existingGenres = em.createQuery("SELECT g FROM Genre g WHERE g.name = :name", Genre.class)
                                    .setParameter("name", genreName)
                                    .setMaxResults(1)
                                    .getResultList();
                            if (!existingGenres.isEmpty()) {
                                genre = existingGenres.get(0);
                            } else {
                                genre = Genre.builder()
                                        .id(genreId.longValue())
                                        .name(genreName)
                                        .build();
                                em.persist(genre);
                            }
                        }
                        genres.add(genre);
                    }
                }
                movie.setGenres(genres);

                CreditsDTO credits = getMovieCredits(movieDTO.getId());
                Set<Person> actors = new HashSet<>();
                Set<Person> directors = new HashSet<>();

                if (credits != null) {
                    // Actors
                    if (credits.getCast() != null) {
                        for (CreditsDTO.CastDTO cast : credits.getCast()) {
                            Person actor = findOrCreatePerson(em, cast.getId(), cast.getName());
                            actors.add(actor);
                        }
                    }
                    // Directors
                    if (credits.getCrew() != null) {
                        for (CreditsDTO.CrewDTO crew : credits.getCrew()) {
                            if ("Director".equalsIgnoreCase(crew.getJob())) {
                                Person director = findOrCreatePerson(em, crew.getId(), crew.getName());
                                directors.add(director);
                            }
                        }
                    }
                }

                movie.setActors(actors);
                movie.setDirectors(directors);

                em.merge(movie); // merge handles both insert and update
            }
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    private Person findOrCreatePerson(EntityManager em, long tmdbId, String name) {
        Person person = em.createQuery("SELECT p FROM Person p WHERE p.tmdbId = :tmdbId", Person.class)
                .setParameter("tmdbId", tmdbId)
                .setMaxResults(1)
                .getResultList()
                .stream()
                .findFirst()
                .orElse(null);
        if (person == null) {
            person = Person.builder()
                    .tmdbId(tmdbId)
                    .name(name)
                    .build();
            em.persist(person);
        }
        return person;
    }
}
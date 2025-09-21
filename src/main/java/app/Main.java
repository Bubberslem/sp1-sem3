package app;

import app.dtos.CreditsDTO;
import app.dtos.MovieDTO;
import app.services.MovieServices;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("Movie API demo");
        MovieServices movieServices = new MovieServices();
        List<MovieDTO> movies = movieServices.getAllMovies();

        movies.forEach(movie -> {
            System.out.println(movie);
            CreditsDTO credits = movieServices.getMovieCredits(movie.getId());
            if (credits != null) {
                System.out.println("Actors:");
                credits.getCast().stream()
                        .limit(5) // show top 5 actors
                        .forEach(cast -> System.out.println(" - " + cast.getName()));
                System.out.println("Directors:");
                credits.getCrew().stream()
                        .filter(crew -> "Director".equalsIgnoreCase(crew.getJob()))
                        .forEach(director -> System.out.println(" - " + director.getName()));
            }
            System.out.println();
        });

    }
}
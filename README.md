# MovieRepo

MovieRepo is a Java-based project that integrates with The Movie Database (TMDb) API to fetch and store information about movies, actors, directors, and genres. The project saves this data in a PostgreSQL database using JPA/Hibernate, enabling you to:

- Retrieve and store movies released in Denmark with Danish as the original language.
- Store and list all actors and directors involved in these movies.
- Store and list all genres, and view all movies within a particular genre.
- Avoid duplicate entries for people and genres by using TMDb IDs and names.

The project demonstrates how to work with external APIs, manage entity relationships, and persist complex data structures in a relational database.

## Technologies

- Java
- JPA/Hibernate
- PostgreSQL
- TMDb API

## Usage

1. Configure your database credentials in `config.properties`.
2. Set your TMDb API key as an environment variable `api_key`.
3. Run the application to fetch and store movie data.

## Structure

- `entities/` - JPA entities for Movie, Person, and Genre.
- `services/` - Logic for fetching from TMDb and storing in the database.
- `Main.java` - Entry point for running the demo.

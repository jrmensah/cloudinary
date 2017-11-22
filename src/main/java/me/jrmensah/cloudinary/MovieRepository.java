package me.jrmensah.cloudinary;

import org.springframework.data.repository.CrudRepository;

public interface MovieRepository extends CrudRepository<Movie, Long> {
    Iterable<Movie> findAllByCastIsIn(Iterable<Actor> actors);
}

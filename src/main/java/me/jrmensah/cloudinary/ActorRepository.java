package me.jrmensah.cloudinary;

import org.springframework.data.repository.CrudRepository;

public interface ActorRepository extends CrudRepository<Actor, Long> {
    Object findAllByMoviesNotContaining(Movie thisMovie);

    Iterable<Actor> findAllByRealnameContainingIgnoreCase(String s);
    Iterable<Actor> findAllByMoviesNotContaining(Movie thisMovie);
}

package library.repositories;

import java.util.List;
import java.util.Optional;

public interface CrudRepository<T, ID> {

    Optional<T> findById(ID id);

    List<T> findAll();

    ID save(T entity);

    void deleteById(ID id);

    default boolean existsById(ID id) {
        return findById(id).isPresent();
    }
}

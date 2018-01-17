package de.beuth.clara.claraSoftware.infrastructure.imports;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import de.beuth.clara.claraSoftware.domain.User;

/**
 * 
 * @author Ray Koeller
 */
public interface ImportedUserJpaRepository extends JpaRepository<User, Long> {

    /**Deletes all Users. Useful for test scenarions in order to start with an empty User set*/
    void deleteAll();

    User save(User User);

    void delete(User User);
    
    Optional<User> findOneById(Long id);
    
    List<User> findAllByOrderByIdDesc();

}

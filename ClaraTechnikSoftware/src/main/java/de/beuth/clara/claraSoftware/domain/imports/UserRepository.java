package de.beuth.clara.claraSoftware.domain.imports;

import java.util.List;
import java.util.Optional;

import de.beuth.clara.claraSoftware.domain.User;

public interface UserRepository {
    /**Deletes all users. Useful for test scenarions in order to start with an empty user set.*/
    void deleteAll();

    /**Gives the user a unique, higher ID and saves the user.
     * @param User User the User to be saved to the repository
     * @return the modified instance*/
    User save(User User);

    /**Deletes the given user.*
     * @param user User the User to be saved to the repository 
     */
    void delete(User user);

    /**Returns the {@link User} object with the given id, if existing.
     * @param id Long the id of the user to be found
     * @return an Optional object, regardless whether a user was found or not
     * @throws IllegalArgumentException  id is null
     */
    Optional<User> find(Long id);
    

    /**Finds all {@link User}s and returns them ordered by descending IDs.
     * @return a List of users
     */
    List<User> findAll();

}

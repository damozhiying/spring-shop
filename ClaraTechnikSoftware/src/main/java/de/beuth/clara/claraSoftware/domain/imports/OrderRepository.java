package de.beuth.clara.claraSoftware.domain.imports;

import java.util.List;
import java.util.Optional;

import de.beuth.clara.claraSoftware.domain.Order;
import de.beuth.clara.claraSoftware.domain.User;

public interface OrderRepository {
	/**Deletes all orders. Useful for test scenarions in order to start with an empty order set.*/
    void deleteAll();

    /**Gives the order a unique, higher ID and saves the order.
     * @param order Order - the order to be saved to the repository
     * @return the modified instance*/
    Order save(Order order);

    /**Deletes the given order.
     * @param order Order the order to be deleted from the repository
     */
    void delete(Order order);

    /**Returns the {@link Order} object with the given id, if existing.
     * @param id Long the id of the order to be found
     * @return an Optional object, regardless whether an order was found or not
     * @throws IllegalArgumentException  id is null
     */
    Optional<Order> find(Long id);
    
    /**Returns the a List of {@link Order} objects associated with the given user.
     * @param orderer User the user of the orders to be found
     * @return a list of orders
     */
    List<Order> findAllByUser(User orderer);
    
    

    /**Finds all {@link Order}s and returns them ordered by descending IDs.
     * @return a List of orders
     */
    List<Order> findAll();
}

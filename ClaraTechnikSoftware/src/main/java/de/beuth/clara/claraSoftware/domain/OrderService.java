package de.beuth.clara.claraSoftware.domain;

import static multex.MultexUtil.create;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.beuth.clara.claraSoftware.domain.imports.OrderItemRepository;
import de.beuth.clara.claraSoftware.domain.imports.OrderRepository;
/**
 * A Service for everything surrounding the order and the order items.
 * @author Lena Noerenberg
 */
@Service
public class OrderService {
	
	// required repos
	final private OrderRepository orderRepo;
	final private OrderItemRepository orderItemRepo;
	
	/**
	 * Creates an OrderService object.
	 * @param orderRepo OrderRepository - the required Order Repository for managing orders
	 * @param orderItemRepo OrderItemrepository - the required Order item Repository for managing
	 * order items
	 */
	@Autowired
	public OrderService(final OrderRepository orderRepo, final OrderItemRepository orderItemRepo) {
		this.orderRepo = orderRepo;
		this.orderItemRepo = orderItemRepo;
	}
	
	/**
	 * Saves an Order object to the Order Repository.
	 * @param order Order - the order to be saved
	 * @return the saved Order
	 */
	public Order save(final Order order) { 
		return orderRepo.save(order);
	}
	
	/**
	 * Saves an OrderItem object to the OrderItem Repository.
	 * @param orderItem OrderItem - the order item to be saved.
	 * @return the saved order item
	 */
	public OrderItem saveOrderItem(final OrderItem orderItem) {
		return orderItemRepo.save(orderItem);
	}
	
	/**
	 * Finds all orders currently present in the database.
	 * @return all available orders
	 */
	public List<Order> findAllOrders(){
		return orderRepo.findAll();
	}
	
	/**
	 * Finds all orders of an owning user.
	 * @param user User - the orderer 
	 * @return a list of orders associated with a specific user
	 */
	public List<Order> findAllOrdersOfUser(final User user){
		return orderRepo.findAllByUser(user);
	}

	/**
	 * Finds a specific order associated with a given order ID.
	 * @param orderId Long - ID of the order to be found
	 * @return the found order
	 */
	public Order findOrder(final Long orderId) {
		return checkForOrderInDB(orderId);
	}
	
	/**
	 * Deletes a specific order associated with a given order ID. All order items 
	 * must be deleted as a consequence to prevent database conflicts.
	 * @param orderId Long - ID of the order to be found
	 */
	public void delete(final Long orderId) {
		final Order toDelete = checkForOrderInDB(orderId);
		final List<OrderItem> orderItems = toDelete.getOrderItems();
		
		for(final OrderItem item : orderItems) {
			deleteOrderItem(item);
		}
		
		orderRepo.delete(toDelete);		
	}
	
	/**
	 * Deletes all orders of this application. To prevent database violations all order items must be deleted.
	 */
	public void deleteAllOrders() {
		orderItemRepo.deleteAll();
		orderRepo.deleteAll();
	}
	
	/**
	 * Deletes a given order item.
	 * @param orderItem OrderItem - the order item to be deleted
	 */
	public void deleteOrderItem(final OrderItem orderItem) {
		orderItemRepo.delete(orderItem);
	}
	

	
	// -------------------------------------------------------
	// Exceptions
	/** Order with ID {0} is not in database. */
	@SuppressWarnings("serial")
	public static class OrderNotInDatabaseExc extends multex.Exc {
	}
	
	// -------------------------------------------------------
	
	/**
	 * checks if the Order is in database
	 * 
	 * @param id Long - the ID of the article to be found
	 * @return Article the found Article
	 * @throws IllegalArgumentException if ID is negative
	 * @throws OrderNotInDatabaseExc if Article is not in database
	 */
	private Order checkForOrderInDB(final Long id) {
		//negative ID
		if(id<1) {
			throw new IllegalArgumentException("ID can not be negative.");
		}
		final Optional<Order> oOrder = orderRepo.find(id);
		if(!oOrder.isPresent()) throw create(OrderNotInDatabaseExc.class, id);
		return oOrder.get();
	}
	
}

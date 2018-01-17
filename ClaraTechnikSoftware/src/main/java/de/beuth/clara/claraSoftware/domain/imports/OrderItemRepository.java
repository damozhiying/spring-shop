package de.beuth.clara.claraSoftware.domain.imports;

import java.util.List;
import java.util.Optional;

import de.beuth.clara.claraSoftware.domain.Article;
import de.beuth.clara.claraSoftware.domain.Order;
import de.beuth.clara.claraSoftware.domain.OrderItem;

public interface OrderItemRepository {

	/** Deletes all {@link OrderItem} objects. */
	void deleteAll();

	/**
	 * Saves the passed object. Linked {@link Article}s or {@link Order}s must be
	 * saved before.
	 * @param orderItem OrderItem - the order item to be saved
	 * @return the saved instance
	 */
	OrderItem save(OrderItem orderItem);

	/** Deletes the given {@link OrderItem} object. 
	 * @param orderItem OrderItem - the order item to be deleted
	 */
	void delete(OrderItem orderItem);

	/**
	 * Returns all {@link OrderItem} objects, which are contained in the order.
	 * @param order Order - the order to find all order items from
	 * @return a list of order items
	 */
	List<OrderItem> findAllOrderItemsInOrder(Order order);
	
	/**
	 * Returns all {@link OrderItem} objects, that contain a specific article.
	 * @param article Article - the article to search for
	 * @return a list of order items
	 */
	List<OrderItem> findAllOrderItemsByArticle(Article article);

	/**
	 * Returns the {@link OrderItem} object for the given order and article, if
	 * the order is containing an order item which includes this article. 
	 * @param order Order - the order to find the order item from
	 * @param article Article - the article to find the order item of
	 * @return an Optional Order item
	 */
	Optional<OrderItem> find(Order order, Article article);



}

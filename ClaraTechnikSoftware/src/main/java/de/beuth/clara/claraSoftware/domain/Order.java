package de.beuth.clara.claraSoftware.domain;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import de.beuth.clara.claraSoftware.domain.base.EntityBase;
import de.beuth.clara.claraSoftware.domain.imports.OrderItemRepository;

/**
 * A class representing an order containing a reference to the associated user.
 * If the field date is not set, an Order object is considered a shopping cart.
 * If the field date is set, an Order object is considered a placed Order.
 * @author Lena Noerenberg
 */
@Entity
@Configurable
@Table(name ="bestellung")
public class Order extends EntityBase<Order> {

	private Timestamp date;
	private Amount totalPrice;

	@ManyToOne
	private User orderer;

	/** Necessary for JPA entities internally. */
	@SuppressWarnings("unused")
	private Order() {
	}

	/**
	 * Creates an Order object.
	 * Since Timestamp is not set, an Order object always starts out as a Shopping Cart.
	 * @param orderer User - the owning Orderer of this Order/Shopping Cart
	 */
	public Order(final User orderer) {
		this.orderer = orderer;
		this.totalPrice = new Amount(0);
	}

	/**
	 * Checks if the Order object in question is a shopping cart or an already placed out Order.
	 * @return true if the Order object is placed; false otherwise
	 */
	public boolean isOrder() {
		return date != null;
	}

	/**
	 * Getter for totalPrice
	 * @return total price of this instance
	 */
	public Amount getTotalPrice() {
		return totalPrice;
	}
	
	/**
	 * Getter for date
	 * @return date of this instance
	 */
	public Timestamp getDate() {
		return date;
	}
	

	/**
	 * Setter for the date field, which consequently makes a placed Order out of a shopping cart.
	 */
	public void setDate() {
		this.date = new Timestamp(System.currentTimeMillis());
	}
	
	/**
	 * Getter for orderer
	 * @return the orderer of this instance
	 */
	public User getOrderer() {
		return orderer;
	}
	
	// required Repository
	@Autowired
	private transient OrderItemRepository orderItemRepo;
	
	/**
	 * Gives an overview of all OrderItem objects associated with this Order object.
	 * @return a list of order items of this order
	 */
	public List<OrderItem> getOrderItems(){
		return orderItemRepo.findAllOrderItemsInOrder(this);
	}
	
	/**
	 * Finds an article associated with an order item of this order.
	 * @param article Article - the article to be found 
	 * @return an Optional object containing the order item, if present
	 */
	public Optional<OrderItem> findArticleInOrder(final Article article){
		return orderItemRepo.find(this,article);
	}
	
	/**
	 * Setter for totalPrice. This method should always be called, if the order is modified.
	 */
	public void setTotalPrice() {
		final List<OrderItem> foundOrderItems = orderItemRepo.findAllOrderItemsInOrder(this);
		Amount newPrice = new Amount(0);
		for(final OrderItem item : foundOrderItems) {
			newPrice = newPrice.plus(item.getItemPrice());
		}
		this.totalPrice = newPrice;
	}

}
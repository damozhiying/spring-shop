package de.beuth.clara.claraSoftware.rest_interface;

import java.sql.Timestamp;

import de.beuth.clara.claraSoftware.domain.Amount;
import de.beuth.clara.claraSoftware.domain.Order;
import de.beuth.clara.claraSoftware.domain.User;

/** Data about an order of CLARA. Usable as Data Transfer Object. 
 * @author Lena Noerenberg
 */
public class OrderResource {
	
	/** Unique ID of the order. */
	public Long id;
	
	/** Complete information of the order.	 */
	public Timestamp date;
	public Amount totalPrice;
	public boolean isOrder;
	
	public User orderer;
	
	
	/** Necessary for Jackson */
	public OrderResource() {
	}
	
	public OrderResource(final Order entity) {
		this.id = entity.getId();
		this.date= entity.getDate();
		this.isOrder = entity.isOrder();
		this.totalPrice = entity.getTotalPrice();
		this.orderer = entity.getOrderer();
	}

}

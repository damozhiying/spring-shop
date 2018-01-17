package de.beuth.clara.claraSoftware.rest_interface;

import de.beuth.clara.claraSoftware.domain.Amount;
import de.beuth.clara.claraSoftware.domain.Article;
import de.beuth.clara.claraSoftware.domain.OrderItem;

/** Data about the position of an order containing articles. Usable as Data Transfer Object.
 * @author Lena Noerenberg
 */
public class OrderItemResource {
	
	public Integer articleAmount;
	public Article article;
	public Amount totalPrice;
	public Long orderId;
	
	/** Neccesary for Jackson*/
	public OrderItemResource() {
	}
	
	public OrderItemResource(final OrderItem entity) {
		this.articleAmount = entity.getArticleAmount();
		this.totalPrice = entity.getItemPrice();
		this.article = entity.getArticle();
		this.orderId = entity.getOrder().getId();
	}
}

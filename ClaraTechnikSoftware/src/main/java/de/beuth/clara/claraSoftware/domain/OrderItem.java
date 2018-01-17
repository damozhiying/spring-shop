package de.beuth.clara.claraSoftware.domain;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Configurable;

import de.beuth.clara.claraSoftware.domain.base.EntityBase;

/**
 * A class representing the position of an order containing the referenced
 * article and the price tag resulting from the number of times this article.
 * @author Lena Noerenberg
 */
@Entity
@Configurable
@Table(name = "bestellposition")
public class OrderItem extends EntityBase<OrderItem> {

	private Integer articleAmount;
	private Amount itemPrice;

	@ManyToOne
	private Article article;

	@ManyToOne
	private Order order;

	/** Necessary for JPA entities internally. */
	@SuppressWarnings("unused")
	private OrderItem() {
	}

	/**
	 * Creates an OrderItem object containing information about an Article and an Order.
	 * @param articleAmount Integer - the number of articles to be specified for this order item
	 * @param article Article - the article to be saved in this order item
	 * @param order Order - the order to which this order item belongs
	 */
	public OrderItem(final Integer articleAmount, final Article article, final Order order) {
		this.setArticleAmount(articleAmount);
		this.setItemPrice(article.getPrice().times(articleAmount));
		this.article = article;
		this.order = order;
	}

	/**
	 * Getter for articleAmount.
	 * @return article amount of this instance.
	 */
	public Integer getArticleAmount() {
		return articleAmount;
	}

	/**
	 * Setter for articleAmount.
	 * @param articleAmount Integer - new value for articleAmount
	 */
	public void setArticleAmount(final Integer articleAmount) {
		this.articleAmount = articleAmount;
	}

	/**
	 * Getter for itemPrice.
	 * @return item price of this instance
	 */
	public Amount getItemPrice() {
		return itemPrice;
	}

	/**
	 * Setter for itemPrice.
	 * @param itemPrice Amount - new value for itemPrice
	 */
	public void setItemPrice(final Amount itemPrice) {
		this.itemPrice = itemPrice;
	}

	/**
	 * Getter for article.
	 * @return the article associated with this order item
	 */
	public Article getArticle() {
		return article;
	}

	/**
	 * Getter for order.
	 * @return the order associated with this order item.
	 */
	public Order getOrder() {
		return order;
	}
}

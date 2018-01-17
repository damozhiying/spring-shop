package de.beuth.clara.claraSoftware.infrastructure;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.beuth.clara.claraSoftware.domain.Article;
import de.beuth.clara.claraSoftware.domain.Order;
import de.beuth.clara.claraSoftware.domain.OrderItem;
import de.beuth.clara.claraSoftware.domain.imports.OrderItemRepository;
import de.beuth.clara.claraSoftware.infrastructure.imports.ImportedOrderItemJpaRepository;

@Service
public class OrderItemJpaRepository implements OrderItemRepository {
	
	private final ImportedOrderItemJpaRepository impl;

	@Autowired
	public OrderItemJpaRepository(final ImportedOrderItemJpaRepository impl) {
		this.impl = impl;
	}
	
	@Override
	public void deleteAll() {
		impl.deleteAll();
	}

	@Override
	public OrderItem save(OrderItem orderItem) {
		return impl.save(orderItem);
	}

	@Override
	public void delete(OrderItem orderItem) {
		impl.delete(orderItem);
	}

	@Override
	public List<OrderItem> findAllOrderItemsInOrder(Order order) {
		return impl.findAllByOrderOrderByIdDesc(order);
	}

	@Override
	public Optional<OrderItem> find(Order order, Article article) {
		return impl.findOneByOrderAndArticle(order,article);
	}

	@Override
	public List<OrderItem> findAllOrderItemsByArticle(Article article) {
		return impl.findAllByArticleOrderByIdDesc(article);
	}

}

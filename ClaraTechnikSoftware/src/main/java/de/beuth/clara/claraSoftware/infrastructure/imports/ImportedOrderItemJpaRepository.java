package de.beuth.clara.claraSoftware.infrastructure.imports;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import de.beuth.clara.claraSoftware.domain.Article;
import de.beuth.clara.claraSoftware.domain.Order;
import de.beuth.clara.claraSoftware.domain.OrderItem;

/**
 * 
 * @author Ray Koeller
 */
public interface ImportedOrderItemJpaRepository extends JpaRepository<OrderItem, Long> {
	
	void deleteAll();

	OrderItem save(OrderItem orderItem);

	void delete(OrderItem orderItem);

	List<OrderItem> findAllByOrderOrderByIdDesc(Order order);
	
	List<OrderItem> findAllByArticleOrderByIdDesc(Article article);

	Optional<OrderItem> findOneByOrderAndArticle(Order order, Article article);

}

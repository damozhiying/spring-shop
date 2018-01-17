package de.beuth.clara.claraSoftware.infrastructure.imports;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import de.beuth.clara.claraSoftware.domain.Order;
import de.beuth.clara.claraSoftware.domain.User;

/**
 * 
 * @author Ray Koeller
 */
public interface ImportedOrderJpaRepository extends JpaRepository<Order, Long> {
	
	void deleteAll();
	
	Order save(Order order);
	
	void delete(Order order);
	
	Optional<Order> findOneById(Long id);
	
	List<Order> findByOrderer(User orderer);
	
	List<Order> findAllByOrderByIdDesc();
	
	
	
}

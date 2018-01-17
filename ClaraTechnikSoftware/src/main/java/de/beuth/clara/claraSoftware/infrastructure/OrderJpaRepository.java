package de.beuth.clara.claraSoftware.infrastructure;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.beuth.clara.claraSoftware.domain.Order;
import de.beuth.clara.claraSoftware.domain.User;
import de.beuth.clara.claraSoftware.domain.imports.OrderRepository;
import de.beuth.clara.claraSoftware.infrastructure.imports.ImportedOrderJpaRepository;

@Service
public class OrderJpaRepository implements OrderRepository{

	private final ImportedOrderJpaRepository impl;
	
	@Autowired
	public OrderJpaRepository(final ImportedOrderJpaRepository impl) {
		this.impl = impl;
	}
	
	@Override
	public void deleteAll() {
		impl.deleteAll();
	}

	@Override
	public Order save(Order order) {
		return impl.save(order);
	}

	@Override
	public void delete(Order order) {
		impl.delete(order);
		
	}

	@Override
	public Optional<Order> find(Long id) {
		return impl.findOneById(id);
	}
	
	

	@Override
	public List<Order> findAll() {
		return impl.findAllByOrderByIdDesc();
	}

	@Override
	public List<Order> findAllByUser(User orderer) {
		return impl.findByOrderer(orderer);
	}
}

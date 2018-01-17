package de.beuth.clara.claraSoftware.domain.imports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This is a domain test scope service for cleaning the database
 * @author Can Heilgermann
 */
@Service
public class CleanUpService {

	private final UserRepository userRepo;

	private final ArticleRepository articleRepo;
	
	private final OrderRepository orderRepo;
	
	private final OrderItemRepository orderItemRepo;
	
	/**
	 * Constructs the cleanup service using the required repositories.
	 * @param userRepo UserRepository - the repository for users
	 * @param articleRepo ArticleRepository - the repository for articles
	 * @param orderRepo OrderRepository - the repository for order
	 * @param orderItemRepo OrderItemRepository - the repository for order items
	 */
	@Autowired
	public CleanUpService(final UserRepository userRepo, final ArticleRepository articleRepo,
			final OrderRepository orderRepo, final OrderItemRepository orderItemRepo) {
		this.userRepo = userRepo;
		this.articleRepo = articleRepo;
		this.orderRepo = orderRepo;
		this.orderItemRepo = orderItemRepo;
	}
	
	/** Flushing the database.
	 */
	public void deleteAll() {
		orderItemRepo.deleteAll();
		articleRepo.deleteAll();
		orderRepo.deleteAll();
		userRepo.deleteAll();
	}
	
}

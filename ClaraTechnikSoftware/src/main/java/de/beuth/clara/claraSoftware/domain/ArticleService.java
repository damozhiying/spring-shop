package de.beuth.clara.claraSoftware.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.beuth.clara.claraSoftware.domain.imports.ArticleRepository;
import de.beuth.clara.claraSoftware.domain.imports.OrderItemRepository;

import static multex.MultexUtil.create;

/**
 * A Service for someone like the admin of this webapplication to handle
 * everything surrounding the articles
 * @author Ahmad Khalil
 */
@Service
public class ArticleService {

	// required repos
	final private ArticleRepository articleRepo;
	final private OrderItemRepository orderItemRepo;

	/**
	 * Creates an ArticleService object.
	 * @param articleRepo ArticleRepository - the required Article Repository
	 * that is being accessed by this Article Service
	 * @param orderItemRepo OrderItemRepository - the required Order item Repository
	 * that is being accessed by this Service
	 */
	@Autowired
	public ArticleService(final ArticleRepository articleRepo, final OrderItemRepository orderItemRepo) {
		this.articleRepo = articleRepo;
		this.orderItemRepo = orderItemRepo;
	}

	/**
	 * Generates a new article-object and saves it into the database
	 * 
	 * @param name String - name of this article
	 * @param description String - description of this article
	 * @param price Amount - price of this article
	 * @return article Article - the created article
	 */
	public Article createArticle(final String name, final String description, final Amount price) {

		checkIfNull(name, description, price);
		checkIfValid(name, description, price);

		final Article article = articleRepo.save(new Article(name, description, price));
		return article;
	}

	/**
	 * Deletes an article corresponding with a given article ID
	 * 
	 * @param articleId - Long - the article to be deleted
	 */
	public void deleteArticle(final Long articleId) {
		final Article toDelete = checkForArticleInDB(articleId);
		final List<OrderItem> orderItems =orderItemRepo.findAllOrderItemsByArticle(toDelete);
		
		for(final OrderItem item : orderItems) {
			orderItemRepo.delete(item);
		}
		
		articleRepo.delete(toDelete);
	}

	/**
	 * Deletes all articles in the database. All order items must be deleted as a consequence.
	 */
	public void deleteAll() {
		orderItemRepo.deleteAll();
		articleRepo.deleteAll();
	}

	/**
	 * Finds an article corresponding with a given ID
	 * 
	 * @param articleId - Long - id of requested article 
	 * @return article - Article - the article that was found
	 * @throws IllegalArgumentException if there was no article associated with this id
	 */
	public Article findArticle(final Long articleId) {

		return checkForArticleInDB(articleId);
	}

	/**
	 * Returns all articles from the database.
	 * 
	 * @return Collection of all articles in database
	 */
	public List<Article> findAllArticles() {
		return articleRepo.findAll();
	}

	/**
	 * @param articleId Long - the ID of the article to be edited
	 * @param name String - name of this article
	 * @param description String - description of this article
	 * @param price Amount - price of this article
	 * @return Article Article - the modified article
	 */
	public Article editArticle(final Long articleId, final String name, final String description, final Amount price) {
		
		final Article article1 = checkForArticleInDB(articleId);

		checkIfNull(name, description, price);

		// testing if changes are valid
		checkIfValid(name, description, price);
		article1.setName(name);
		article1.setDescription(description);
		article1.setPrice(price);

		return article1;
	}

	/**
	 * Edits an Article. Works like the HTTP-Method PATCH.
	 * @param articleId Long - id of requested article
	 * @param name String - name of this article
	 * @param description String - description of this article
	 * @param price Amount - price of this article
	 * @return Article Article - the modified article
	 * @throws NameExc if the name-property of articleResource is smaller than 2 or bigger than 20
	 * @throws DescriptionExc if description-property of articleResource is smaller than 10 or
	 * bigger than 100
	 * @throws AmountExc if the price-property of articleResource is negative
	 */
	public Article patchArticle(final Long articleId, final String name, final String description, final Amount price) {
		final Article article1 = checkForArticleInDB(articleId);

		// testing what should be changed and testing if changes are valid
		if (name != null) {
			if (name.length() < 2 || 20 < name.length() ){
				throw create(NameExc.class, name);
			}
			final List<Article> allArticles = articleRepo.findAll();
			for (final Article article : allArticles) {
				if (article.getName().equals(name))
					throw create(ArticleNameExc.class, name);
			}
			article1.setName(name); // change
		}

		if (description != null) {
			if (description.length() < 10 || 100 < description.length() ) {
				throw create(DescriptionExc.class, description);
			}
			article1.setDescription(description); // change
		}

		if (price != null) {
			if(price.toDouble() < 0.0)
				throw create(AmountExc.class, price);
			article1.setPrice(price); // change
		}

		return article1;
	}

	// -------------------------------------------------------
	// Exceptions

	/** Article {0} is not in database. */
	@SuppressWarnings("serial")
	public static class ArticleNotInDatabaseExc extends multex.Exc {
	}

	/** Illegal name {0}. Name should consist of at least 3 characters and of 20 at most.*/
	@SuppressWarnings("serial")
	public static class NameExc extends multex.Exc {
	}

	/** Articlename {0} exists already. */
	@SuppressWarnings("serial")
	public static class ArticleNameExc extends multex.Exc {
	}

	/** Description {0} is too short or too long. Description should consist of at least 21 characters and of 100 at most. */
	@SuppressWarnings("serial")
	public static class DescriptionExc extends multex.Exc {
	}
	

	 /** Amount {0} is not a valid value. */
	 @SuppressWarnings("serial")
	 public static class AmountExc extends multex.Exc {
	 }

	// ---------------------------------------------------------

	 /**
	  * checks if the parameters are null
	  * @param name String - Name of the article
	  * @param description String - Description of the article
	  * @param price Amount - Pricetag of the article
	  * @throws IllegalArgumentException if one of the given arguments is null
	  */
	private void checkIfNull(final String name, final String description, final Amount price) {
		if (name == null)
			throw new IllegalArgumentException("name is null");
		if (description == null)
			throw new IllegalArgumentException("description is null");
		if (price == null)
			throw new IllegalArgumentException("price is null");
	}

	/**
	 * validates the parameters on illegal characters, correctness and/or 
	 * appropriate length
	 * @param name String - Name of the article
	 * @param description String - Description of the article
	 * @param price Amount - Pricetag of the article
	 * @throws NameExc if the name-property of articleResource is smaller than 2 or bigger than 20
	 * @throws DescriptionExc if description-property of articleResource is smaller than 10 or
	 * bigger than 100
	 * @throws AmountExc if the price-property of articleResource is negative
	 */
	private void checkIfValid(final String name, final String description, final Amount price) {

		// check if articlename-length is valid
		if (name.length() < 2 || 20 < name.length() )
			throw create(NameExc.class, name);
		
		// check if articlename already exists
		final List<Article> allarticle = articleRepo.findAll();
		for (final Article article : allarticle) {
			if (article.getName().equals(name))
				throw create(ArticleNameExc.class, name);
		}

		// check if description-length is valid
		if (description.length() < 10 || 100 < description.length())
			throw create(DescriptionExc.class, description);
		
		// check if price is negative
		if(price.toDouble() < 0.0)
			throw create(AmountExc.class, price);
	}
	/**
	 * checks if the Article is in database
	 * 
	 * @param id Long - the ID of the article to be found
	 * @return Article the found Article
	 * @throws IllegalArgumentException if ID is negative
	 * @throws ArticleNotInDatabaseExc if Article is not in database
	 */
	private Article checkForArticleInDB(final Long id) {
		//negative ID
		if(id<1) {
			throw new IllegalArgumentException("ID can not be negative.");
		}
		final Optional<Article> oArticle = articleRepo.find(id);
		if(!oArticle.isPresent()) throw create(ArticleNotInDatabaseExc.class, id);
		return oArticle.get();
	}

}

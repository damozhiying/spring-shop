package de.beuth.clara.claraSoftware.rest_interface;

import de.beuth.clara.claraSoftware.domain.Amount;
import de.beuth.clara.claraSoftware.domain.Article;

/** Data about a user of CLARA. Usable as Data Transfer Object. 
 * @author Ray Koeller
 */
public class ArticleResource {

	/** Unique ID of the article. */
	public Long id;

	/** Complete information of the article. */
	public String name;
	public String description;
	public Amount price;

	/** Necessary for Jackson */
	public ArticleResource() {
	}

	/** Constructs a ArticleResource with the data of the passed Article entity.
	 * @param entity Article the article that will be parsed into a Resource 
	 */
	public ArticleResource(final Article entity) {
		this.id = entity.getId();
		this.name = entity.getName();
		this.description = entity.getDescription();
		this.price = entity.getPrice();
	}

}
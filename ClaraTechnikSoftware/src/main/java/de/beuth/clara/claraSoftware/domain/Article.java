package de.beuth.clara.claraSoftware.domain;


import javax.persistence.Entity;

import org.springframework.beans.factory.annotation.Configurable;

import de.beuth.clara.claraSoftware.domain.base.EntityBase;

/**
 * A class representing an article containing name, description and price
 * @author Ray Koeller
 */
@Entity
@Configurable
public class Article extends EntityBase<Article> {
    
    private String name;
    private String description;
    private Amount price;
    
    
    
    /**Necessary for JPA entities internally.*/
    @SuppressWarnings("unused")
	private Article() {}
    
    /**
     * Creates an article object
     * @param name - String - name of this article
     * @param description - String - description of this article
     * @param price - Amount - price of this article
     */
    public Article(final String name, final String description, final Amount price) {
	this.name = name;
	this.description = description;
	this.price = price;
    }

    /**
     * Getter for name
     * @return name - String - the name of this article 
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for name
     * @param name - String - the name of this article 
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Getter for description
     * @return description - String - description of this article
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter for description
     * @param description - String - description of this article
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Getter for price
     * @return price - Amount - price of this article
     */
    public Amount getPrice() {
        return price;
    }

    /**
     * Setter for price. Every order item must also be modified in price.
     * @param price - Amount - price of this article
     */
    public void setPrice(final Amount price) {
        this.price = price;
    }
}

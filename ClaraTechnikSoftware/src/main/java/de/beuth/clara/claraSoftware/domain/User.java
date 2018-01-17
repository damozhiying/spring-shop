package de.beuth.clara.claraSoftware.domain;

import static multex.MultexUtil.create;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.beuth.clara.claraSoftware.domain.base.EntityBase;
/**A user of ClaraSoftware along with some methods it can do.
 * @author Ahmad Kasbah
 */
@Entity
@Configurable
@Table(name = "benutzer")
public class User extends EntityBase<User> {

	private String username;
	private String password;
	private String honorifics;
	private String name;
	private String firstname;
	
	@Embedded
	private Address address;
	
	@ElementCollection
	@CollectionTable
	private List<Bankaccount> bankaccounts;

	/** Necessary for JPA entities internally. */
	@SuppressWarnings("unused")
	private User() {
	}

	/**
	 * Creates a User with honorifics, name, first name, username and password
	 * @param honorifics String - honorifics of this user
	 * @param name String - last name of this user
	 * @param firstname String - firstname of this user
	 * @param username String - username of this user
	 * @param password String - password of this user
	 * @param adress Adress - adress of this user
	 */
	public User(final String honorifics,final String name,final String firstname,final String username,final String password, final Address adress) {
		this.setHonorifics(honorifics);
		this.setName(name);
		this.setFirstname(firstname);
		this.setUsername(username);
		this.setPassword(password);
		this.bankaccounts= new ArrayList<Bankaccount>();
		setAdress(adress);
	}

	/**
	 * Getter for honorifics
	 * @return the honorifics of this user
	 */
	public String getHonorifics() {
		return honorifics;
	}
	
	/**
	 * Setter for honorifics
	 * @param honorifics String - honorifics of this user
	 */
	public void setHonorifics(final String honorifics) {
		this.honorifics = honorifics;
	}

	/**
	 * Getter for name
	 * @return the last name of this user
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setter for name
	 * @param name String - name of this user
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Getter for first name
	 * @return the first name of this user
	 */
	public String getFirstname() {
		return firstname;
	}

	/**
	 * Setter for first name
	 * @param firstname String - first name of this user
	 */
	public void setFirstname(final String firstname) {
		this.firstname = firstname;
	}
	
	/**
	 * Getter for username
	 * @return the username of this user
	 */
	public String getUsername() {
		return username;
	}
	
	/**
	 * Setter for username
	 * @param username String - the username of this user
	 */
	public void setUsername(final String username) {
		this.username = username;
	}
	
	/**
	 * Getter for password
	 * @return the password of this user
	 */
	public String getPassword() {
		return password;
	}
	
	/**
	 * Setter for password
	 * @param password String - the password of this user
	 */
	public void setPassword(final String password) {
		this.password = password;
	}
	
	// required services
	@Autowired
	private transient ArticleService articleService;
	@Autowired
	private transient OrderService orderService;

	/**
	 * Searches all articles for a keyword containing that keyword in either their description or their name.
	 * @param keyword String - the keyword to be used
	 * @return a List of all articles containing that keyword in either their description or their name
	 * @throws IllegalArgumentException if the keyword is only contained of spaced, is empty or null
	 */
	public List<Article> searchArticles(final String keyword) {
		if(keyword == null || keyword.trim().isEmpty()) {
			throw new IllegalArgumentException("The keyword to be searched for should not be empty or null! ");
		}
		final String keywordtrimmed = keyword.trim();
		final List<Article> allArticles = articleService.findAllArticles();
		final List<Article> toRemove = new ArrayList<>();
		
		for(final Article article : allArticles) {
			if(!article.getDescription().toLowerCase().contains(keywordtrimmed.toLowerCase()) && !article.getName().toLowerCase().contains(keywordtrimmed.toLowerCase())) toRemove.add(article);
		}
		
		for(final Article remove : toRemove ) {
			allArticles.remove(remove);
		}
		return allArticles;
	}
	
	/**
	 * Creates a new Order object as a shopping cart.
	 * @return the creared Order object.
	 * @throws ShoppingCartAlreadyExistingExc if this user already has a shopping cart
	 */
	Order createNewShoppingCart() {
		final List<Order> allOrdersOfUser = orderService.findAllOrdersOfUser(this);
		for(final Order order : allOrdersOfUser) {
			if(!order.isOrder()) throw create(ShoppingCartAlreadyExistingExc.class, order.getId());
		}
		return orderService.save(new Order(this));
	}
	
	
	/**
	 * Returns the OrderItem objects that are currently in the shopping cart of this user.
	 * @return a list of order items
	 */
	public List<OrderItem> viewShoppingCart(){
		return getShoppingCart().getOrderItems();
	}
	
	/**
	 * Returns the current shopping cart as an Order object. Creates a new one, if there was none before.
	 * @return an order
	 */
	@JsonIgnore
	public Order getShoppingCart() {
		final List<Order> allOrdersOfUser = orderService.findAllOrdersOfUser(this);
		Order toreturn = null;
		for(final Order order : allOrdersOfUser) {
			if(!order.isOrder())toreturn= order;
		}
		return toreturn;
	}
	
	/**
	 * Gives an overview of all order items contained in an order.
	 * @param orderId Long - ID of order to be viewed
	 * @return a List of order items
	 * @throws UserNotOrdererExc if the order is being accessed by a non-owning user
	 */
	public List<OrderItem> viewOrder(final Long orderId){
		final Order order = orderService.findOrder(orderId);   // look if order in order repository
		if(!order.getOrderer().sameIdentityAs(this)) throw create(UserNotOrdererExc.class, this.getId());
		return order.getOrderItems();
	}
	
	/**
	 * Gives an overview of all orders of this user.
	 * @return a list of all orders of this user
	 */
	public List<Order> viewAllOrders(){
		return orderService.findAllOrdersOfUser(this);
	}

	/**
	 * Adss a specific number of an Article to a shopping cart.
	 * If the order is already containing an order item with that article, the article amount 
	 * of that order item is increased by the specified argument articleAmount of this method.
	 * @param orderId Long - ID of the order to be modified
	 * @param articleId Long - ID of the article in question
	 * @param articleAmount - number of articles to be added to order
	 * @return the order item containing the added article
	 * @throws IllegalArgumentException if articleAmount is null order smaller than 1 
	 */
	public OrderItem addToShoppingCart(final Long orderId, final Long articleId, final Integer articleAmount) {
		if(articleAmount == null || articleAmount.intValue() <= 0) {
			throw new IllegalArgumentException("Article Amount should not be smaller than 1 or null!");
		}
		final Article article = articleService.findArticle(articleId);  // check if article is in repository
		final Order order = checkOrderInfo(orderId);
		final Optional<OrderItem> oOrderItem = order.findArticleInOrder(article);  // check for orderItem
		
		if(!oOrderItem.isPresent()) {
			final OrderItem orderItem =orderService.saveOrderItem(new OrderItem(articleAmount, article, order));
			order.setTotalPrice();
			return orderItem;
		}
		else {
			final OrderItem orderItem = oOrderItem.get();
			orderItem.setArticleAmount(orderItem.getArticleAmount()+articleAmount);
			orderItem.setItemPrice(orderItem.getArticle().getPrice().times(orderItem.getArticleAmount()));
			order.setTotalPrice();
			return orderItem;
		}
		
	}

	/**
	 * Removes a specific number of an Articles from a shopping cart.
	 * Removes the whole order item from that order if articleAmount is equal in value to
	 * the amount of articles contained in the order item in question.
	 * @param orderId Long - ID of the order to be modified
	 * @param articleId Long - ID of the article in question
	 * @param articleAmount - number of articles to be removed from order
	 * @throws IllegalArgumentException if articleAmount is null order smaller than 1 
	 * @throws OrderItemNotFoundExc if the order does not contain the specified article
	 * @throws ArticleAmountNotValidExc if the number to be removed exceeds the number of articles
	 * contained in the order item
	 */
	public void removeFromShoppingCart(final Long orderId, final Long articleId, final Integer articleAmount) {
		if(articleAmount == null || articleAmount.intValue() <= 0) {
			throw new IllegalArgumentException("Article Amount should not be smaller than 1 or null!");
		}
		final Article article = articleService.findArticle(articleId);
		final Order order = checkOrderInfo(orderId);
		final Optional<OrderItem> oOrderItem = order.findArticleInOrder(article);

		if(!oOrderItem.isPresent()) {
			throw create(OrderItemNotFoundExc.class, article.getId());
		}
		
		final OrderItem orderItem = oOrderItem.get();
		
		if(orderItem.getArticleAmount().intValue() < articleAmount.intValue()) {
			throw create(ArticleAmountNotValidExc.class, articleAmount);
		}
		else if(orderItem.getArticleAmount().intValue() ==articleAmount.intValue()) {
			orderService.deleteOrderItem(orderItem);
			order.setTotalPrice();
		}
		else {
			orderItem.setArticleAmount(orderItem.getArticleAmount().intValue() - articleAmount.intValue());
			orderItem.setItemPrice(orderItem.getArticle().getPrice().times(orderItem.getArticleAmount()));
			order.setTotalPrice();
		}

	}
	
	/**
	 * Removes an Order Object from this User, regardless of whether the Order Object was
	 * a shopping cart or an Order. 
	 * Removes the order items in that order as well to prevent database conflicts.
	 * @param orderId Long - ID of the order to be canceled
	 * @throws UserNotOrdererExc if the order is being accessed by a non-owning user
	 */
	public void cancelOrder(final Long orderId) {
		final Order order = orderService.findOrder(orderId);
		if(!order.getOrderer().sameIdentityAs(this)) throw create(UserNotOrdererExc.class, this.getId());

		if(!order.isOrder()) {
			orderService.delete(orderId);		
			createNewShoppingCart();
		}
		else {
			orderService.delete(orderId);
		}
	}

	/**
	 * Sets the date field of a shopping cart, making it an order
	 * @param orderId Long - ID of the shopping cart to be placed as an Order
	 * @return the placed Order 
	 */
	public Order order(final Long orderId) {
		final Order order = checkOrderInfo(orderId);
		if(order.getOrderItems().isEmpty()) throw create(OrderIsEmptyExc.class, orderId);
		order.setDate();
		createNewShoppingCart();
		return order;
	}

	/**
	 * Getter for Bank accounts
	 * @return a list of all bank accounts associated with this user
	 */
	public List<Bankaccount> getBankaccounts() {
		return bankaccounts;
	}

	/**
	 * adds a bankaccount to this user
	 * @param iban String - International Bank Account Number
	 * @param bic String - Bank Identifier Code
	 * @throws NoMoreBankaccountsExc if there are more bankaccounts associated with this user than allowed
	 */
	public void addBankaccount(final String iban, final String bic) {
		if(bankaccounts.size() >= 4) {
			throw create(NoMoreBankaccountsExc.class, this.getId());
		}
		checkBankaccountInfo(iban, bic);
		bankaccounts.add(new Bankaccount(iban,bic));
	}
	
	/**
	 * deletes a bankaccount from this user
	 * @param bankaccountnumber int - Index of that bank account
	 * @throws IllegalArgumentException if bankaccountnumber is bigger than the total amount of bankaccounts for this user
	 */
	public void deleteBankaccount(final int bankaccountnumber) {
		if (bankaccounts.isEmpty() || bankaccountnumber > bankaccounts.size() - 1) {
			throw new IllegalArgumentException("There is no such Bankaccount associated with the User-ID: " + this.getId());
		}
		bankaccounts.remove(bankaccountnumber);
	}
	
	/**
	 * Deletes all bank accounts associated with user, if there are any
	 */
	public void deleteAllBankaccounts() {
		int size = bankaccounts.size();
		for(int i = 0; i < size;i++) {
			deleteBankaccount(0);
		}
	}
	/**
	 * Setter for adress
	 * @param adress Adress containing postalcode, city, street and housenumber information
	 * @throws AdressExc if adress is null
	 */
	public void setAdress (final Address adress) {
		if(adress == null) throw create(AdressExc.class, adress);
		
		final Integer postcode = adress.getPostcode();
		final String city = adress.getCity();
		final String street = adress.getStreet();
		final String housenumber = adress.getHousenumber();
		
		
		checkAdressInfo(postcode,city,street,housenumber);
		
		this.address = adress;		
	}

	/**
	 * Getter for adress
	 * @return an Adress Object with postcode, city, steet and housenumber properties
	 */
	public Address getAdress() {
		return address;
	}

	//---------------------------------------------------
	// Exceptions
	
	
	/** Cannot place order with ID {0}. Shopping Cart is empty. There is nothing to order. */
	@SuppressWarnings("serial")
	public static class OrderIsEmptyExc extends multex.Exc {
	}
	
	
	/** There is already a Shopping-Cart with ID {0} for this user. There is no need to create a new one.*/
	@SuppressWarnings("serial")
	public static class ShoppingCartAlreadyExistingExc extends multex.Exc {
	}
	
	/** Order with ID {0} has already been put out. Fill a new Shopping cart or cancel Order {0}.*/
	@SuppressWarnings("serial")
	public static class OrderExc extends multex.Exc{
	}
	
	/** User with ID {0} is not authorized for this order.*/
	@SuppressWarnings("serial")
	public static class UserNotOrdererExc extends multex.Exc{
	}
	
	/** Order item does not contain {0} articles. {0} is too big a number.*/
	@SuppressWarnings("serial")
	public static class ArticleAmountNotValidExc extends multex.Exc{
	}
	
	
	/** OrderItem not found. Article with article ID {0} does not exist in this order.*/
	@SuppressWarnings("serial")
	public static class OrderItemNotFoundExc extends multex.Exc{
	}
	
	/** User {0} has already reached the maximum amount of Bank-Accounts.(Max. 4) */
	@SuppressWarnings("serial")
	public static class NoMoreBankaccountsExc extends multex.Exc {
	}
	
	/**
	 * {0} is not a valid representation of that information. 
	 * IBAN should consist of DE at the start with at least 10 numbers following. BIC should be at
	 * least 6 characters long and contains only numbers and letters.
	 */
	@SuppressWarnings("serial")
	public static class BankaccountInfoExc extends multex.Exc {
	}
	
	/** Bankaccount is missing {0} */
	@SuppressWarnings("serial")
	public static class MissingBankaccountInfoExc extends multex.Exc {
	}
	
	/**
	 * {0} is not a valid representation of {1}. 
	 * Postcode should have five digits and must not be negative.
	 * City and Street properties should be only consisting of letters, while Housenumber can 
	 * contain letters (e.g. Ruheplatz Straße 23a).
	 */
	@SuppressWarnings("serial")
	public static class InvalidAdressInfoExc extends multex.Exc{
	}
	
	/**
	 * Adress must not be {0}.
	 */
	@SuppressWarnings("serial")
	public static class AdressExc extends multex.Exc{
	}
	//---------------------------------------------------
	// helpful methods
	
	/**
	 * Checks if BIC and IBAN of parameter bankaccount are valid representations
	 * @param iban - String - International Bank Account Number
	 * @param bic - String - Bank Identifier Code
	 * @throws MissingBankaccountInfoExc if bic or iban of bankaccount are empty or null
	 * @throws BankaccountInfoExc if bic or iban of bankaccount are too short or have invalid characters in them
	 */
	private void checkBankaccountInfo(final String iban, final String bic) {
		// check if bank account info is not empty or null
		if (bic == null || bic.isEmpty())
			throw create(MissingBankaccountInfoExc.class, "BIC");

		if (iban == null || iban.isEmpty())
			throw create(MissingBankaccountInfoExc.class, "IBAN");

		if (iban.length() < 12 || bic.length() < 6)
			throw create(BankaccountInfoExc.class, iban, bic);

		if (!iban.toLowerCase().startsWith("de"))
			throw create(BankaccountInfoExc.class, iban);

		String ibanNumbers = iban.substring(2);

		if (!ibanNumbers.matches("[0-9]+"))
			throw create(BankaccountInfoExc.class, iban);

		if (!bic.matches("[a-zA-Z0-9]+"))
			throw create(BankaccountInfoExc.class, bic);
	}
	
	/**
	 * Checks if Adress information given as arguments are valid and not null
	 * @param postcode int postal code
	 * @param city String name of the city
	 * @param street String name of the street without housenumbers
	 * @param housenumber String specific housenumber
	 * @throws InvalidAdressInfoExc if one parameter is represented wrongly
	 */
	private void checkAdressInfo(final Integer postcode, final String city, final String street, final String housenumber) {
		
		if(postcode == null || String.valueOf(postcode).length() != 5 || postcode.intValue() < 0 ) {
			throw create(InvalidAdressInfoExc.class, postcode,"Postcode");
		}
		if(city == null || !(city.matches("[a-zA-Z]+"))) {
			throw create(InvalidAdressInfoExc.class, city, "City");
		}
		if(street == null || !(street.matches("[a-zA-Z]+"))) {
			throw create(InvalidAdressInfoExc.class, street, "Street");
		}
		if(housenumber == null || !(housenumber.matches("[0-9]+[a-zA-Z]*")) || housenumber.length() > 5) {
			throw create(InvalidAdressInfoExc.class, housenumber, "housenumber");
		}	
		
	}
	
	
	/**
	 * Checks if a order is being accessed by the correct user and if the order has already been placed
	 * @param orderId Long - ID of the order to be checked
	 * @return the valid Order Object
	 * @throws UserNotOrdererExc if the order is being accessed by a non-owning user
	 * @throws OrderExc if the order has already been placed 
	 */
	private Order checkOrderInfo(final Long orderId) {
		final Order order = orderService.findOrder(orderId);
		if(!order.getOrderer().sameIdentityAs(this)) throw create(UserNotOrdererExc.class, this.getId());
		if(order.isOrder()) throw create(OrderExc.class, orderId);
		return order;
		
	}


}

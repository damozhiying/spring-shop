package de.beuth.clara.claraSoftware.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import de.beuth.clara.claraSoftware.domain.OrderService.OrderNotInDatabaseExc;
import de.beuth.clara.claraSoftware.domain.User.AdressExc;
import de.beuth.clara.claraSoftware.domain.User.ArticleAmountNotValidExc;
import de.beuth.clara.claraSoftware.domain.User.BankaccountInfoExc;
import de.beuth.clara.claraSoftware.domain.User.InvalidAdressInfoExc;
import de.beuth.clara.claraSoftware.domain.User.MissingBankaccountInfoExc;
import de.beuth.clara.claraSoftware.domain.User.NoMoreBankaccountsExc;
import de.beuth.clara.claraSoftware.domain.User.OrderIsEmptyExc;
import de.beuth.clara.claraSoftware.domain.User.OrderItemNotFoundExc;
import de.beuth.clara.claraSoftware.domain.User.UserNotOrdererExc;
import de.beuth.clara.claraSoftware.domain.imports.CleanUpService;


/**
 * A test for the user domain class.
 * @author Ray Koeller
 */
@RunWith(SpringRunner.class)
//@DataJpaTest
//@AutoConfigureTestDatabase
@SpringBootTest
public class UserTest {
	@Autowired
	private transient ArticleService articleService;
	@Autowired
	private transient OrderService orderService;
	@Autowired 
	private transient UserService userService;
	@Autowired
	private transient CleanUpService cleanUpService;

	final private Address adress = new Address(13347, "Berlin", "Ruheplatzstrasse", "23b");
	final private String honorifics = "Herr";
	final private String firstname = "Ray";
	final private String name = "Koeller";
	final private String password = "password";
	final private String username = "username";
	private User user;
	
	
	
	@Before
	public void cleanUp() {
		cleanUpService.deleteAll();
		user =userService.createUser(honorifics, name, firstname,username,password, adress);
	}
	
	@After
	public void cleanUpAfter() {
		cleanUpService.deleteAll();
	}

	@Test
	public void checkAttributesTest() {
		assertEquals(firstname, user.getFirstname());
		assertEquals(name, user.getName());
		assertEquals(honorifics, user.getHonorifics());
		assertEquals(username, user.getUsername());
		assertEquals(password, user.getPassword());
		assertEquals(adress, user.getAdress());
	}

	@Test
	public void searchArticlesTest() {
		try{
			user.searchArticles("");
			fail("IllegalArgumentException expected");
		} catch (IllegalArgumentException expected) {
		}
		try{
			user.searchArticles("     ");
			fail("IllegalArgumentException expected");
		} catch (IllegalArgumentException expected) {
		}
		try{
			user.searchArticles(null);
			fail("IllegalArgumentException expected");
		} catch (IllegalArgumentException expected) {
		}
		
		articleService.createArticle("DELL XPS13", "This is a description for dell.", new Amount(99));
		articleService.createArticle("DELL XPS15", "This is a description for dell bliblablub.", new Amount(99));
		
		assertEquals(0, user.searchArticles("KeywordNotPresent").size());
		assertEquals(2, user.searchArticles("DELL").size()); // name 
		assertEquals(1, user.searchArticles(" 13 ").size()); // name
		assertEquals(1, user.searchArticles("bliblablub").size()); // description test
	}
	
	@Test 
	public void viewOrderTest() {
		final List<Order> ordersOfUser = user.viewAllOrders();
		final List<Order> ordersOfAnotherUser =  userService.createUser(honorifics, name, firstname, "username65", password, adress).viewAllOrders();		
		for(final Order order : ordersOfAnotherUser) {
			assertNotEquals(order.getOrderer(), user);
			try {
				user.viewOrder(order.getId());
				fail("UserNotOrdererExc expected");
			} catch (UserNotOrdererExc expected) {
			}
		}
		for(final Order order : ordersOfUser) {
			assertEquals(order.getOrderer(), user);
			assertEquals(order.getOrderItems(), user.viewOrder(order.getId()));
		}
	}
	
	@Test
	public void addToShoppingCartTest() {
		final Article article = articleService.createArticle("DELL XPS15", "This is a description for dell bliblablub.", new Amount(99));
		final Order shoppingcart = user.getShoppingCart();
		try {
			// trying articleAmount null
			user.addToShoppingCart(shoppingcart.getId(), article.getId(), null);
			fail("IllegalArgumentException expected");
		} catch(IllegalArgumentException expected) {
		}
		try {
			// trying articleAmount negative
			user.addToShoppingCart(shoppingcart.getId(), article.getId(), -1);
			fail("IllegalArgumentException expected");
		} catch(IllegalArgumentException expected) {
		}
	
		// adding one article to the shopping cart
		user.addToShoppingCart(shoppingcart.getId(),article.getId(), 1);
		assertEquals(shoppingcart.getOrderItems().size(), 1);
		user.addToShoppingCart(shoppingcart.getId(),article.getId(), 1);

		// adding again one article
		user.addToShoppingCart(shoppingcart.getId(),article.getId(), 1);
		assertEquals(shoppingcart.getOrderItems().size(), 1);
		
		// generating a user with another shopping cart to check access rights
		final User user2 = userService.createUser(honorifics, name, firstname, username+"2", password, adress);
		final Order shoppingcart2 = user2.getShoppingCart();
		
		try {
			user.addToShoppingCart(shoppingcart2.getId(), article.getId(), 2);
			fail("UserNotOrdererExc expected");
		} catch(UserNotOrdererExc expected) {
		}
		 
	}
	
	@Test
	public void removeFromShoppingCartTest() {
		final Article article = articleService.createArticle("DELL XPS15", "This is a description for dell bliblablub.", new Amount(99));
		final Order shoppingcart = user.getShoppingCart();
		user.addToShoppingCart(shoppingcart.getId(), article.getId(), 1);
		try {
			// trying articleAmount null
			user.addToShoppingCart(shoppingcart.getId(), article.getId(), null);
			fail("IllegalArgumentException expected");
		} catch(IllegalArgumentException expected) {
		}
		try {
			// trying articleAmount negative
			user.removeFromShoppingCart(shoppingcart.getId(), article.getId(), -1);
			fail("IllegalArgumentException expected");
		} catch(IllegalArgumentException expected) {
		}
		assertEquals(shoppingcart.getOrderItems().size(),1);
		//removing one article
		user.removeFromShoppingCart(shoppingcart.getId(), article.getId(), 1);
		assertEquals(shoppingcart.getOrderItems().size(),0);
		
		// adding two, removing 1 .. orderitems should not have changed
		user.addToShoppingCart(shoppingcart.getId(), article.getId(), 2);
		assertEquals(shoppingcart.getOrderItems().size(),1);
		user.removeFromShoppingCart(shoppingcart.getId(), article.getId(), 1);
		assertEquals(shoppingcart.getOrderItems().size(),1);
		
		try {
			// trying to remove 2 from an order item with only 1 article  inside
			user.removeFromShoppingCart(shoppingcart.getId(), article.getId(), 10);
			fail("ArticleAmountNotValidExc expected");
		} catch(ArticleAmountNotValidExc expected) {
		}
		
		try {
			// trying to remove from an order item which is not in the shopping cart
			final Article article2 = articleService.createArticle("An article again", "this is a description to this article.", new Amount(99));
			user.removeFromShoppingCart(shoppingcart.getId(), article2.getId(), 1);
			fail("OrderItemNotFoundExc expected");
		} catch(OrderItemNotFoundExc expected) {
		}	
	}
	
	@Test
	public void orderTest() {
		final Order shoppingcart = user.getShoppingCart();
		final List<OrderItem> itemsInCart = user.viewShoppingCart();
		assertTrue(itemsInCart.size() == 0);
		try {
			//try to place an empty order
			user.order(shoppingcart.getId());
			fail("OrderIsEmptyExc expected");
		} catch(OrderIsEmptyExc expected) {
		}
		final Article article = articleService.createArticle("DELL XPS15", "This is a description for dell bliblablub.", new Amount(99));
		user.addToShoppingCart(shoppingcart.getId(), article.getId(), 1);
		assertTrue(user.viewShoppingCart().size() ==1);
		assertTrue(!shoppingcart.isOrder());
		shoppingcart.setDate();	
		assertTrue(shoppingcart.isOrder());
	}

	
	@Test
	public void cancelOrderTest() {
		final Order shoppingcart = user.getShoppingCart();
		final User user2 =userService.createUser(honorifics, name, firstname,username+"22",password, adress);
		try {
			user2.cancelOrder(shoppingcart.getId());
			fail("UserNotOrdererException expected");
		} catch(UserNotOrdererExc expected) {
		}
		
		user.cancelOrder(shoppingcart.getId());
		try {
			orderService.findOrder(shoppingcart.getId());
			fail("OrderNotInDatabaseExc expected");
		} catch(OrderNotInDatabaseExc expected) {
		}
		
	}

	@Test
	public void addBankaccountTest() {
		// try assigning a valid bankaccount to a user
		user.addBankaccount("DE1234567890", "DRESDEFF100");
		assertEquals(1, user.getBankaccounts().size());
		assertEquals(user.getBankaccounts().get(0).getBic(),"DRESDEFF100");
		assertEquals(user.getBankaccounts().get(0).getIban(),"DE1234567890");
		user.deleteAllBankaccounts();

		try {
			// iban is empty
			user.addBankaccount("", "DRESDEFF100");
			fail("MissingBankaccountInfoExc expected");
		} catch (MissingBankaccountInfoExc expected) {
		}
		try {
			// iban is null
			user.addBankaccount(null, "DRESDEFF100");
			fail("MissingBankaccountInfoExc expected");
		} catch (MissingBankaccountInfoExc expected) {
		}
		try {
			// bic is empty
			user.addBankaccount("DE1234567890", "");
			fail("MissingBankaccountInfoExc expected");
		} catch (MissingBankaccountInfoExc expected) {
		}
		try {
			// bic is null
			user.addBankaccount("DE1234567890", null);
			fail("MissingBankaccountInfoExc expected");
		} catch (MissingBankaccountInfoExc expected) {
		}
		try {
			// iban is too short
			user.addBankaccount("DE55", "DRESDEFF100");
			fail("BankaccountInfoExc expected");
		} catch (BankaccountInfoExc expected) {
		}
		try {
			// bic is too short
			user.addBankaccount("DE1234567890", "DRESS");
			fail("BankaccountInfoExc expected");
		} catch (BankaccountInfoExc expected) {
		}
		try {
			// iban doesnt start with de
			user.addBankaccount("EN1234567890", "DRESSDEFF100");
			fail("BankaccountInfoExc expected");
		} catch (BankaccountInfoExc expected) {
		}
		try {
			// iban starts with de, but has other characters than digits after that
			user.addBankaccount("DE12E456Z890", "DRESSDEFF100");
			fail("BankaccountInfoExc expected");
		} catch (BankaccountInfoExc expected) {
		}
		try {
			// bic has illegal characters
			user.addBankaccount("DE12E456Z890", "DRES?DEFF100");
			fail("BankaccountInfoExc expected");
		} catch (BankaccountInfoExc expected) {
		}
		try {
			// bic with only illegal characters
			user.addBankaccount("DE12E456Z890", "????????");
			fail("BankaccountInfoExc expected");
		} catch (BankaccountInfoExc expected) {
		}
		try {
			// try assigning too many bankaccounts to a user
			for (int i = 0; i < 5; i++) {
				user.addBankaccount("DE1234567890", "DRESDEFF100");
			}
			fail("NoMoreBankaccountsExc expected");
		} catch (NoMoreBankaccountsExc expected) {
		}
	}

	@Test
	public void deleteBankaccountExceptionsTest() {
		try {
			// deleting something when there is no bankaccount associated with this user
			user.deleteBankaccount(0);
			fail("IllegalArgumentException expected");
		} catch (IllegalArgumentException expected) {
		}
		try {
			// deleting something, that is out of reach, e.g. since only up to for
			// bankaccounts can be saved
			for (int i = 0; i < 4; i++) {
				user.addBankaccount("DE1234567890", "DRESDEFF100");
			}
			user.deleteBankaccount(4);
			fail("IllegalArgumentException expected");
		} catch (IllegalArgumentException expected) {
		}

		// deleting one bankaccount
		assertEquals(4, user.getBankaccounts().size());
		user.deleteBankaccount(0);
		assertEquals(3, user.getBankaccounts().size());

		// testing deleteAllBankaccounts
		user.deleteAllBankaccounts();
		assertEquals(0, user.getBankaccounts().size());
	}

	@Test
	public void setAdressExceptionsTest() {
		// setting a correct adress
		user.setAdress(new Address(13347, "city", "street", "1"));
		assertEquals(13347, user.getAdress().getPostcode().intValue());
		assertEquals("city", user.getAdress().getCity());
		assertEquals("street", user.getAdress().getStreet());
		assertEquals("1", user.getAdress().getHousenumber());

		try {
			// setting adress null
			user.setAdress(null);
			fail("AdressExc expected");
		} catch (AdressExc expected) {
		}

		try {
			// setting postcode null
			user.setAdress(new Address(null, "ciy", "street", "1"));
			fail("InvalidAdressInfoExc expected");
		} catch (InvalidAdressInfoExc expected) {
		}
		try {
			// setting city null
			user.setAdress(new Address(13347, null, "street", "1"));
			fail("InvalidAdressInfoExc expected");
		} catch (InvalidAdressInfoExc expected) {
		}
		try {
			// setting street null
			user.setAdress(new Address(13347, "city", null, "1"));
			fail("InvalidAdressInfoExc expected");
		} catch (InvalidAdressInfoExc expected) {
		}
		try {
			// setting housenumber null
			user.setAdress(new Address(13347, "city", "street", null));
			fail("InvalidAdressInfoExc expected");
		} catch (InvalidAdressInfoExc expected) {
		}

		try {
			// postcode too short (must be 5 of length)
			user.setAdress(new Address(1334, "city", "street", "1"));
			fail("InvalidAdressInfoExc expected");
		} catch (InvalidAdressInfoExc expected) {
		}
		try {
			// postcode too long (must be 5 of length)
			user.setAdress(new Address(133456, "city", "street", "1"));
			fail("InvalidAdressInfoExc expected");
		} catch (InvalidAdressInfoExc expected) {
		}
		try {
			// postcode negative
			user.setAdress(new Address(-1334, "city", "street", "1"));
			fail("InvalidAdressInfoExc expected");
		} catch (InvalidAdressInfoExc expected) {
		}
		try {
			// city has invalid characters (only letters allowed)
			user.setAdress(new Address(13347, "city1", "street", "1"));
			fail("InvalidAdressInfoExc expected");
		} catch (InvalidAdressInfoExc expected) {
		}
		try {
			// street has invalid characters (only letters allowed)
			user.setAdress(new Address(13347, "city", "street1", "1"));
			fail("InvalidAdressInfoExc expected");
		} catch (InvalidAdressInfoExc expected) {
		}
		try {
			// housenumber is not correct (must have at least one digit)
			user.setAdress(new Address(13347, "city", "street", "a"));
			fail("InvalidAdressInfoExc expected");
		} catch (InvalidAdressInfoExc expected) {
		}
		try {
			// housenumber is too long (maximum length of 5)
			user.setAdress(new Address(13347, "city", "street", "12345a"));
			fail("InvalidAdressInfoExc expected");
		} catch (InvalidAdressInfoExc expected) {
		}

		// trying the extreme
		user.setAdress(new Address(13347, "T", "S", "1"));
		assertTrue(13347 == user.getAdress().getPostcode());
		assertEquals("T", user.getAdress().getCity());
		assertEquals("S", user.getAdress().getStreet());
		assertEquals("1", user.getAdress().getHousenumber());

		user.setAdress(new Address(13347, "W", "S", "1234a"));
		assertTrue(13347 == user.getAdress().getPostcode());
		assertEquals("W", user.getAdress().getCity());
		assertEquals("S", user.getAdress().getStreet());
		assertEquals("1234a", user.getAdress().getHousenumber());
	}

}

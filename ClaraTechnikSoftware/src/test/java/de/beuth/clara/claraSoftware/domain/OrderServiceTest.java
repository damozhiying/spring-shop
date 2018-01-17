package de.beuth.clara.claraSoftware.domain;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import de.beuth.clara.claraSoftware.domain.OrderService.OrderNotInDatabaseExc;
import de.beuth.clara.claraSoftware.domain.imports.CleanUpService;


/** Test driver for the service class {@linkplain OrderService}
 * @author Ahmad Kasbah
 */
@RunWith(SpringRunner.class)
// @DataJpaTest
// @AutoConfigureTestDatabase
@SpringBootTest
public class OrderServiceTest {

	final private Address address = new Address(13347, "Berlin", "Ruheplatzstrasse", "23b");

	@Autowired
	private CleanUpService cleanUpService;

	@Autowired
	private OrderService orderService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ArticleService articleService;
	
	@Before
	public void cleanUp() {
		cleanUpService.deleteAll();
	}
	
	@After 
	public void cleanUpAgain() {
		cleanUpService.deleteAll();
	}

	@Test
	public void findAllOrdersTest() {
		assertTrue(orderService.findAllOrders().size() == 0);
		userService.createUser("Herr", "Kasbah", "Ahmad", "username", "password", address);
		assertTrue(orderService.findAllOrders().size() == 1);
	}
	
	@Test
	public void findOrderTest() {
		try {
			// negative ID
			orderService.findOrder(-1L);
			fail("IllegalArgumentException expected");
		} catch(IllegalArgumentException expected) {
		}
		try {
			// order not present
			orderService.findOrder(9L);
			fail("OrderNotInDatabaseExc expected");
		} catch(OrderNotInDatabaseExc expected) {
		}
		
		final User user =userService.createUser("Herr", "Kasbah", "Ahmad", "username", "password", address);
		orderService.findOrder(user.getShoppingCart().getId());
		assertEquals(user.getShoppingCart().getDate(), null);
		assertEquals(user.getShoppingCart().getTotalPrice(), new Amount(0));
	}
	
	@Test
	public void saveOrderItemTest() {
		final User user =userService.createUser("Herr", "Kasbah", "Ahmad", "username", "password", address);
		final Article article =articleService.createArticle("DELL XPS13", "This is a description for dell.", new Amount(99));
		assertEquals(user.getShoppingCart().getTotalPrice(), new Amount(0));
		orderService.saveOrderItem(new OrderItem(1, article, user.getShoppingCart()));
		assertTrue(user.getShoppingCart().getOrderItems().size() ==1);
	}
	
	@Test
	public void deleteOrderTest() {
		final User user =userService.createUser("Herr", "Kasbah", "Ahmad", "username", "password", address);
		final Article article =articleService.createArticle("DELL XPS13", "This is a description for dell.", new Amount(99));
		orderService.saveOrderItem(new OrderItem(1, article, user.getShoppingCart()));
		
		assertTrue(orderService.findAllOrders().size() ==1);
		assertTrue(user.getShoppingCart().getOrderItems().size() ==1);
		
		orderService.delete(user.getShoppingCart().getId());
		
		assertTrue(orderService.findAllOrders().size() ==0);
	}
	 
	@Test
	public void deleteAllOrdersTest() {
		final User user =userService.createUser("Herr", "Kasbah", "Ahmad", "username", "password", address);
		final Article article =articleService.createArticle("DELL XPS13", "This is a description for dell.", new Amount(99));
		orderService.saveOrderItem(new OrderItem(1, article, user.getShoppingCart()));
		
		assertTrue(orderService.findAllOrders().size() ==1);
		assertTrue(user.getShoppingCart().getOrderItems().size() ==1);
		
		orderService.deleteAllOrders();
		
		assertTrue(orderService.findAllOrders().size() ==0);
	}
}

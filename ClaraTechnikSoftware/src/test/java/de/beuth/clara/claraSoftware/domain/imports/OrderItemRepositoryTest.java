/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.beuth.clara.claraSoftware.domain.imports;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import de.beuth.clara.claraSoftware.domain.Address;
import de.beuth.clara.claraSoftware.domain.Amount;
import de.beuth.clara.claraSoftware.domain.Article;
import de.beuth.clara.claraSoftware.domain.Order;
import de.beuth.clara.claraSoftware.domain.OrderItem;
import de.beuth.clara.claraSoftware.domain.User;

/** Test driver for the Rich Domain Object {@linkplain UserRepository} 
 * @author Can Heilgermann
 */
@RunWith(SpringRunner.class)
// @DataJpaTest
// @AutoConfigureTestDatabase
@SpringBootTest
public class OrderItemRepositoryTest {

	
	@Autowired
	private CleanUpService cleanUpService;
	
	@Autowired
	private OrderRepository orderRepo;
	@Autowired 
	private UserRepository userRepo;
	@Autowired
	private ArticleRepository articleRepo;
	@Autowired 
	private OrderItemRepository orderItemRepo;
	
	// pre defined users
	final User ray = new User("Herr", "Clara", "Ray", "user", "password", 
			new Address(13347, "Berlin", "Hellostrassee", "12a"));
	final User lena =new User("Frau", "Clara", "Lena", "user2", "password", 
			new Address(13347, "Berlin", "Hellostrassee", "12a"));
	final User can = new User("Herr", "Clara", "Can", "user1", "password", 
			new Address(13347, "Berlin", "Hellostrassee", "12a"));
	
	// pre defined articles
	final Article graphicsCard = new Article("GTX1080ti",
			"A Graphics Card of the 10. Generation of NVIDIA GTX.",new Amount(900));
	final Article processor = new Article("Intel I9",
			"10 core Intel processor of the 8. Generation.", new Amount(400));
	
	
	@Before
	public void cleanUp() {
		cleanUpService.deleteAll();
	}
	
	@After
	public void cleanUpAgain() {
		cleanUpService.deleteAll();
	}

	@Test
	public void createOrderItemCheckPropertiesTest() {
		
		// preparations
		final User userRay = userRepo.save(ray);
		final Order raysOrder = orderRepo.save(new Order(userRay));
		final Article articleGTX = articleRepo.save(graphicsCard);
		
		final OrderItem GTXitem = orderItemRepo.save(new OrderItem(1,articleGTX, raysOrder));
		
		assertNotNull(GTXitem);
		assertNotNull(GTXitem.getId());
		assertTrue(GTXitem.getId() > 0);
		Optional<OrderItem> found = orderItemRepo.find(raysOrder, articleGTX);
		assertEquals(found.get(), GTXitem);
		assertEquals(found.get().getArticle(), articleGTX);
		assertTrue(found.get().getArticleAmount().intValue() ==1);
		assertEquals(found.get().getOrder(), raysOrder);
		assertEquals(found.get().getOrder().getOrderer(), userRay);
		assertEquals(found.get().getItemPrice(), articleGTX.getPrice());
	}

	@Test
	public void findOrderItemTest() {
		
		// preparations
		final User userRay = userRepo.save(ray);
		final User userLena = userRepo.save(lena);
		final Order raysOrder = orderRepo.save(new Order(userRay));
		final Order lenasOrder = orderRepo.save(new Order(userLena));
		final Article articleGTX = articleRepo.save(graphicsCard);
		final Article articleINTEL = articleRepo.save(processor);
		
		final OrderItem GTXRay = orderItemRepo.save(new OrderItem(1, articleGTX,raysOrder));
		final OrderItem INTELLena = orderItemRepo.save(new OrderItem(1, articleINTEL,lenasOrder));
		

		// Order finden
		final Optional<OrderItem> foundRayOrderItem = orderItemRepo.find(raysOrder, articleGTX);
		final Optional<OrderItem> foundLenaOrderItem = orderItemRepo.find(lenasOrder, articleINTEL);
		final Optional<OrderItem> notFoundLenaOrderItem = orderItemRepo.find(lenasOrder, articleGTX);
		
		// Test: correct?
		assertEquals(foundRayOrderItem.get(), GTXRay);
		assertEquals(foundLenaOrderItem.get(), INTELLena);
		assertEquals(notFoundLenaOrderItem, Optional.empty());
	}
	
	@Test
	public void findAllOrderItemInOrderTest() {
		// preparations
		final User userRay = userRepo.save(ray);
		final User userLena = userRepo.save(lena);
		final Order raysOrder = orderRepo.save(new Order(userRay));
		final Order lenasOrder = orderRepo.save(new Order(userLena));
		final Article articleGTX = articleRepo.save(graphicsCard);
		final Article articleINTEL = articleRepo.save(processor);
		
		final OrderItem GTXRay = orderItemRepo.save(new OrderItem(1, articleGTX,raysOrder));
		final OrderItem INTELRay = orderItemRepo.save(new OrderItem(2,articleINTEL, raysOrder));
		final OrderItem INTELLena = orderItemRepo.save(new OrderItem(1, articleINTEL,lenasOrder));
		
		final List<OrderItem> foundRaysOrderItems = orderItemRepo.findAllOrderItemsInOrder(raysOrder);
		final List<OrderItem> foundLenasOrderItems = orderItemRepo.findAllOrderItemsInOrder(lenasOrder);
		
		assertTrue(foundRaysOrderItems.size()  == 2);
		assertTrue(foundLenasOrderItems.size() == 1);

		assertTrue(foundRaysOrderItems.contains(GTXRay) && foundRaysOrderItems.contains(INTELRay));
		assertTrue(foundLenasOrderItems.contains(INTELLena));
		assertFalse(foundLenasOrderItems.contains(GTXRay));
		
	}

	@Test
	public void findAllOrderItemsByArticleTest() {
		
		// preparations
		final User userRay = userRepo.save(ray);
		final User userLena = userRepo.save(lena);
		final Order raysOrder = orderRepo.save(new Order(userRay));
		final Order lenasOrder = orderRepo.save(new Order(userLena));
		final Article articleGTX = articleRepo.save(graphicsCard);
		final Article articleINTEL = articleRepo.save(processor);
		
		final OrderItem GTXRay = orderItemRepo.save(new OrderItem(1, articleGTX,raysOrder));
		final OrderItem INTELRay = orderItemRepo.save(new OrderItem(2,articleINTEL, raysOrder));
		final OrderItem INTELLena = orderItemRepo.save(new OrderItem(1, articleINTEL,lenasOrder));
		
		List<OrderItem> foundAll = orderItemRepo.findAllOrderItemsByArticle(articleINTEL);
		// Liste darf nicht leer sein, 3 Order vorhanden
		assertFalse(foundAll.isEmpty());
		assertEquals(foundAll.size(), 2);

		// Liste muss die folgenden User beinhalten
		assertTrue(foundAll.contains(INTELRay));
		assertTrue(foundAll.contains(INTELLena));
		assertFalse(foundAll.contains(GTXRay));

		// ID descending
		assertEquals(foundAll.get(0), INTELLena);
		assertEquals(foundAll.get(1), INTELRay);
	}

	@Test
	public void deleteOrderTest() {

		// preparations
		final User userRay = userRepo.save(ray);
		final User userLena = userRepo.save(lena);
		final Order raysOrder = orderRepo.save(new Order(userRay));
		final Order lenasOrder = orderRepo.save(new Order(userLena));
		final Article articleGTX = articleRepo.save(graphicsCard);
		final Article articleINTEL = articleRepo.save(processor);
		
		final OrderItem GTXRay = orderItemRepo.save(new OrderItem(1, articleGTX,raysOrder));
		final OrderItem INTELRay = orderItemRepo.save(new OrderItem(2,articleINTEL, raysOrder));
		final OrderItem INTELLena = orderItemRepo.save(new OrderItem(1, articleINTEL,lenasOrder));
		
		
		// Test: noch alle vorhanden
		assertEquals(orderItemRepo.find(raysOrder, articleGTX).get(), GTXRay);
		assertEquals(orderItemRepo.find(raysOrder, articleINTEL).get(), INTELRay);
		assertEquals(orderItemRepo.find(lenasOrder, articleINTEL).get(), INTELLena);

		
		// Test: Loeschen eines Users, die anderen muessen aber noch vorhanden sein
		orderItemRepo.delete(GTXRay);

		assertEquals(orderItemRepo.find(raysOrder, articleGTX), Optional.empty());
		assertEquals(orderItemRepo.find(raysOrder, articleINTEL).get(), INTELRay);
		assertEquals(orderItemRepo.find(lenasOrder, articleINTEL).get(), INTELLena);
	}

	@Test
	public void deleteAllOrdersTest() {

		// preparations
		final User userRay = userRepo.save(ray);
		final User userLena = userRepo.save(lena);
		final Order raysOrder = orderRepo.save(new Order(userRay));
		final Order lenasOrder = orderRepo.save(new Order(userLena));
		final Article articleGTX = articleRepo.save(graphicsCard);
		final Article articleINTEL = articleRepo.save(processor);
				
		final OrderItem GTXRay = orderItemRepo.save(new OrderItem(1, articleGTX,raysOrder));
		final OrderItem INTELRay = orderItemRepo.save(new OrderItem(2,articleINTEL, raysOrder));
		final OrderItem INTELLena = orderItemRepo.save(new OrderItem(1, articleINTEL,lenasOrder));
		
		// Test: noch alle vorhanden
		assertEquals(orderItemRepo.find(raysOrder, articleGTX).get(), GTXRay);
		assertEquals(orderItemRepo.find(raysOrder, articleINTEL).get(), INTELRay);
		assertEquals(orderItemRepo.find(lenasOrder, articleINTEL).get(), INTELLena);
		
		orderItemRepo.deleteAll();

		assertEquals(orderItemRepo.find(raysOrder, articleGTX), Optional.empty());
		assertEquals(orderItemRepo.find(raysOrder, articleINTEL), Optional.empty());
		assertEquals(orderItemRepo.find(lenasOrder, articleINTEL), Optional.empty());
	}
	
}

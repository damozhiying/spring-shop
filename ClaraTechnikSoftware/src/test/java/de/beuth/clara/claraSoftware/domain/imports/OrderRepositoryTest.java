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
import de.beuth.clara.claraSoftware.domain.Order;
import de.beuth.clara.claraSoftware.domain.User;

/** Test driver for the Rich Domain Object {@linkplain UserRepository} 
 * @author Can Heilgermann 
 */
@RunWith(SpringRunner.class)
// @DataJpaTest
// @AutoConfigureTestDatabase
@SpringBootTest
public class OrderRepositoryTest {

	@Autowired
	private CleanUpService cleanUpService;
	
	@Autowired
	private OrderRepository orderRepo;
	@Autowired 
	private UserRepository userRepo;
	
	// pre defined users
	final User ray = new User("Herr", "Clara", "Ray", "user", "password", 
			new Address(13347, "Berlin", "Hellostrassee", "12a"));
	final User lena =new User("Frau", "Clara", "Lena", "user2", "password", 
			new Address(13347, "Berlin", "Hellostrassee", "12a"));
	final User can = new User("Herr", "Clara", "Can", "user1", "password", 
			new Address(13347, "Berlin", "Hellostrassee", "12a"));
	
	
	@Before
	public void cleanUp() {
		cleanUpService.deleteAll();
	}
	
	@After 
	public void cleanUpAgain() {
		cleanUpService.deleteAll();
	}

	@Test
	public void createOrderCheckPropertiesTest() {
		

		final User userRay = userRepo.save(ray);
		final Order raysOrder = orderRepo.save(new Order(userRay));
		assertNotNull(raysOrder);
		assertNotNull(raysOrder.getId());
		assertTrue(raysOrder.getId() > 0);
		Optional<Order> found = orderRepo.find(raysOrder.getId());
		assertEquals(found.get(), raysOrder);
		assertEquals(raysOrder.getOrderer(), userRay);

	}

	@Test
	public void findOrderTest() {

		final User userRay = userRepo.save(ray);
		final User userLena = userRepo.save(lena);
		final User userCan = userRepo.save(can);
		
		final Order raysOrder = orderRepo.save(new Order(userRay));
		final Order lenasOrder = orderRepo.save(new Order(userLena));
		final Order cansOrder = orderRepo.save(new Order(userCan));

		// Order finden
		final Optional<Order> foundRayOrder = orderRepo.find(raysOrder.getId());
		final Optional<Order> foundLenaOrder = orderRepo.find(lenasOrder.getId());
		final Optional<Order> foundCanOrder = orderRepo.find(cansOrder.getId());

		// Test: ist der gefundene User richtig?
		assertEquals(foundRayOrder.get(), raysOrder);
		assertEquals(foundRayOrder.get().getOrderer(), userRay);
		assertEquals(foundLenaOrder.get(), lenasOrder);
		assertEquals(foundLenaOrder.get().getOrderer(), userLena);
		assertEquals(foundCanOrder.get(), cansOrder);
		assertEquals(foundCanOrder.get().getOrderer(), userCan);

	}
	
	@Test
	public void findByUserTest() {
		final User userRay = userRepo.save(ray);
		final User userLena = userRepo.save(lena);
		final User userCan = userRepo.save(can);
		
		final Order raysOrder = orderRepo.save(new Order(userRay));
		final Order lenasOrder = orderRepo.save(new Order(userLena));
		final Order lenasOrder2 = orderRepo.save(new Order(userLena));
		
		final List<Order> foundRaysOrders = orderRepo.findAllByUser(userRay);
		final List<Order> foundLenasOrders = orderRepo.findAllByUser(userLena);
		final List<Order> foundCansOrders = orderRepo.findAllByUser(userCan);
		
		assertTrue(foundRaysOrders.size()  == 1);
		assertTrue(foundLenasOrders.size() == 2);
		assertTrue(foundCansOrders.size() == 0);
		
		assertTrue(foundRaysOrders.contains(raysOrder));
		assertTrue(foundLenasOrders.contains(lenasOrder) && foundLenasOrders.contains(lenasOrder2));
		assertFalse(foundCansOrders.contains(lenasOrder));
		
	}

	@Test
	public void findAllOrdersTest() {
		
		final User userRay = userRepo.save(ray);
		final User userLena = userRepo.save(lena);
		final User userCan = userRepo.save(can);
		
		final Order raysOrder = orderRepo.save(new Order(userRay));
		final Order lenasOrder = orderRepo.save(new Order(userLena));
		final Order cansOrder = orderRepo.save(new Order(userCan));
		
		List<Order> foundAll = orderRepo.findAll();
		// Liste darf nicht leer sein, 3 Order vorhanden
		assertFalse(foundAll.isEmpty());
		assertEquals(foundAll.size(), 3);
		assertEquals(foundAll.size(), orderRepo.findAll().size());

		// Liste muss die folgenden User beinhalten
		assertTrue(foundAll.contains(raysOrder));
		assertTrue(foundAll.contains(lenasOrder));
		assertTrue(foundAll.contains(cansOrder));

		// ID descending
		assertEquals(foundAll.get(0), cansOrder);
		assertEquals(foundAll.get(1), lenasOrder);
		assertEquals(foundAll.get(2), raysOrder);
	}

	@Test
	public void deleteOrderTest() {

		final User userRay = userRepo.save(ray);
		final User userLena = userRepo.save(lena);
		final User userCan = userRepo.save(can);
		
		final Order raysOrder = orderRepo.save(new Order(userRay));
		final Order lenasOrder = orderRepo.save(new Order(userLena));
		final Order cansOrder = orderRepo.save(new Order(userCan));
		
		
		// Test: noch alle vorhanden
		assertEquals(orderRepo.find(raysOrder.getId()).get(), raysOrder);
		assertEquals(orderRepo.find(lenasOrder.getId()).get(), lenasOrder);
		assertEquals(orderRepo.find(cansOrder.getId()).get(), cansOrder);

		// Test: Loeschen eines Users, die anderen muessen aber noch vorhanden sein
		orderRepo.delete(orderRepo.find(cansOrder.getId()).get());

		assertEquals(orderRepo.find(cansOrder.getId()), Optional.empty());
		assertEquals(orderRepo.find(lenasOrder.getId()).get(), lenasOrder);
		assertEquals(orderRepo.find(raysOrder.getId()).get(), raysOrder);
	}

	@Test
	public void deleteAllOrdersTest() {

		final User userRay = userRepo.save(ray);
		final User userLena = userRepo.save(lena);
		final User userCan = userRepo.save(can);
		
		final Order raysOrder = orderRepo.save(new Order(userRay));
		final Order lenasOrder = orderRepo.save(new Order(userLena));
		final Order cansOrder = orderRepo.save(new Order(userCan));
		
		assertTrue(!(orderRepo.findAll().isEmpty())); // is not empty
		assertEquals(orderRepo.findAll().size(), 3);
		orderRepo.deleteAll();

		assertTrue(orderRepo.findAll().isEmpty()); // should be empty
		assertEquals(orderRepo.findAll().size(), 0);
		assertEquals(orderRepo.find(cansOrder.getId()), Optional.empty());
		assertEquals(orderRepo.find(lenasOrder.getId()), Optional.empty());
		assertEquals(orderRepo.find(raysOrder.getId()), Optional.empty());
	}
	
}

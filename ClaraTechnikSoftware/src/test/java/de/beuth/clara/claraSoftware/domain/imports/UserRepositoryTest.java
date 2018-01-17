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
import de.beuth.clara.claraSoftware.domain.User;

/** Test driver for the Rich Domain Object {@linkplain UserRepository} 
 * @author Can Heilgermann
 */
@RunWith(SpringRunner.class)
// @DataJpaTest
// @AutoConfigureTestDatabase
@SpringBootTest
public class UserRepositoryTest {

	@Autowired
	private CleanUpService cleanUpService;
	
	@Autowired
	private UserRepository userRepo;
	
	final private Address address = new Address(13347, "Berlin", "Ruheplatzstrasse", "23b");

	@Before
	public void cleanUp() {
		cleanUpService.deleteAll();
	}
	
	@After
	public void cleanUpAgain() {
		cleanUpService.deleteAll();
	}

	@Test
	public void createUserCheckPropertiesTest() {
		final User ray = userRepo.save(new User("Herr", "Clara", "Ray", "user", "password", address));

		assertNotNull(ray);
		assertNotNull(ray.getId());
		assertTrue(ray.getId() > 0);
		Optional<User> found = userRepo.find(ray.getId());
		assertEquals(found.get(), ray);

	}

	@Test
	public void findUserTest() {

		final User can = userRepo.save(new User("Herr", "Clara", "Can", "user1", "password", address));
		final User lena = userRepo.save(new User("Frau", "Clara", "Lena", "user2", "password", address));
		final User ahmad1 = userRepo.save(new User("Herr", "Clara", "Ahmad", "user3", "password", address));
		final User ray = userRepo.save(new User("Herr", "Clara", "Ray", "user4", "password", address));
		final User ahmad2 = userRepo.save(new User("Herr", "Clara", "Ahmad", "user5", "password", address));

		// Test auf ID
		assertTrue(can.getId() > 0);
		assertTrue(lena.getId() > 0);
		assertTrue(ahmad1.getId() > 0);
		assertTrue(ray.getId() > 0);
		assertTrue(ahmad2.getId() > 0);

		// User finden
		final Optional<User> foundCan = userRepo.find(can.getId());
		final Optional<User> foundLena = userRepo.find(lena.getId());
		final Optional<User> foundAhmad1 = userRepo.find(ahmad1.getId());
		final Optional<User> foundRay = userRepo.find(ray.getId());
		final Optional<User> foundAhmad2 = userRepo.find(ahmad2.getId());

		// Test: ist der gefundene User richtig?
		assertEquals(foundCan.get(), can);
		assertEquals(foundLena.get(), lena);
		assertEquals(foundAhmad1.get(), ahmad1);
		assertEquals(foundRay.get(), ray);
		assertEquals(foundAhmad2.get(), ahmad2);

	}

	@Test
	public void findAllUsersTest() {

		final User can = userRepo.save(new User("Herr", "Clara", "Can", "user1", "password", address));
		final User lena = userRepo.save(new User("Frau", "Clara", "Lena", "user2", "password", address));
		final User ahmad1 = userRepo.save(new User("Herr", "Clara", "Ahmad", "user3", "password", address));
		final User ray = userRepo.save(new User("Herr", "Clara", "Ray", "user4", "password", address));
		final User ahmad2 = userRepo.save(new User("Herr", "Clara", "Ahmad", "user5", "password", address));

		List<User> foundAll = userRepo.findAll();
		// Liste darf nicht leer sein, 5 User vorhanden
		assertFalse(foundAll.isEmpty());
		assertEquals(foundAll.size(), 5);

		// Liste muss die folgenden User beinhalten
		assertTrue(foundAll.contains(can));
		assertTrue(foundAll.contains(lena));
		assertTrue(foundAll.contains(ahmad1));
		assertTrue(foundAll.contains(ray));
		assertTrue(foundAll.contains(ahmad2));

		assertEquals(foundAll.get(4), can);
		assertEquals(foundAll.get(3), lena);
		assertEquals(foundAll.get(2), ahmad1);
		assertEquals(foundAll.get(1), ray);
		assertEquals(foundAll.get(0), ahmad2);
	}

	@Test
	public void deleteUserTest() {

		final User can = userRepo.save(new User("Herr", "Clara", "Can", "user1", "password", address));
		final User lena = userRepo.save(new User("Frau", "Clara", "Lena", "user2", "password", address));
		final User ahmad1 = userRepo.save(new User("Herr", "Clara", "Ahmad", "user3", "password", address));
		final User ray = userRepo.save(new User("Herr", "Clara", "Ray", "user4", "password", address));
		final User ahmad2 = userRepo.save(new User("Herr", "Clara", "Ahmad", "user5", "password", address));

		// Test: noch alle vorhanden
		assertEquals(userRepo.find(can.getId()).get(), can);
		assertEquals(userRepo.find(lena.getId()).get(), lena);
		assertEquals(userRepo.find(ahmad1.getId()).get(), ahmad1);
		assertEquals(userRepo.find(ray.getId()).get(), ray);
		assertEquals(userRepo.find(ahmad2.getId()).get(), ahmad2);

		// Test: Loeschen eines Users, die anderen muessen aber noch vorhanden sein
		userRepo.delete(userRepo.find(can.getId()).get());

		assertEquals(userRepo.find(can.getId()), Optional.empty());
		assertEquals(userRepo.find(lena.getId()).get(), lena);
		assertEquals(userRepo.find(ahmad1.getId()).get(), ahmad1);
		assertEquals(userRepo.find(ray.getId()).get(), ray);
		assertEquals(userRepo.find(ahmad2.getId()).get(), ahmad2);
	}

	@Test
	public void deleteAllUsersTest() {

		final User can = userRepo.save(new User("Herr", "Clara", "Can", "user1", "password", address));
		final User lena = userRepo.save(new User("Frau", "Clara", "Lena", "user2", "password", address));
		final User ahmad1 = userRepo.save(new User("Herr", "Clara", "Ahmad", "user3", "password", address));
		final User ray = userRepo.save(new User("Herr", "Clara", "Ray", "user4", "password", address));
		final User ahmad2 = userRepo.save(new User("Herr", "Clara", "Ahmad", "user5", "password", address));

		assertTrue(!(userRepo.findAll().isEmpty())); // is not empty
		assertEquals(userRepo.findAll().size(), 5);
		userRepo.deleteAll();

		assertTrue(userRepo.findAll().isEmpty()); // should be empty
		assertEquals(userRepo.findAll().size(), 0);
		assertEquals(userRepo.find(can.getId()), Optional.empty());
		assertEquals(userRepo.find(lena.getId()), Optional.empty());
		assertEquals(userRepo.find(ahmad1.getId()), Optional.empty());
		assertEquals(userRepo.find(ray.getId()), Optional.empty());
		assertEquals(userRepo.find(ahmad2.getId()), Optional.empty());

	}

}

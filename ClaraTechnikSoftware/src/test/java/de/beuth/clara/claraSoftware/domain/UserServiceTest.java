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
import org.springframework.transaction.annotation.Transactional;

import de.beuth.clara.claraSoftware.domain.UserService.HonorificsExc;
import de.beuth.clara.claraSoftware.domain.UserService.NameExc;
import de.beuth.clara.claraSoftware.domain.UserService.PasswordExc;
import de.beuth.clara.claraSoftware.domain.UserService.UserNotInDatabaseExc;
import de.beuth.clara.claraSoftware.domain.UserService.UsernameExc;
import de.beuth.clara.claraSoftware.domain.imports.CleanUpService;

/** Test driver for the service class {@linkplain UserService} 
 * @author Lena Noerenberg
 */
@RunWith(SpringRunner.class)
// @DataJpaTest
// @AutoConfigureTestDatabase
@SpringBootTest
@Transactional
public class UserServiceTest {

	@Autowired
	private UserService userService;
	
	@Autowired
	private CleanUpService cleanUpService;
	
	final private Address adress = new Address(13347, "Berlin", "Ruheplatzstrasse", "23b");

	@Before
	public void cleanUp() {
		cleanUpService.deleteAll();
	}
	
	@After
	public void cleanUpAgain() {
		cleanUpService.deleteAll();
	}

	@Test
	public void createUserExceptions() {

		// honorifics can not be null
		try {
			userService.createUser(null, "Kasbah", "Ahmad", "AhmadK4", "password", adress);
			fail("IllegalArgumentException expected");
		} catch (IllegalArgumentException expected) {
		}

		// name can not be null
		try {
			userService.createUser("Herr", null, "Ahmad", "AhmadK4", "password", adress);
			fail("IllegalArgumentException expected");
		} catch (IllegalArgumentException expected) {
		}

		// first name can not be null
		try {
			userService.createUser("Herr", "Kasbah", null, "AhmadK4", "password", adress);
			fail("IllegalArgumentException expected");
		} catch (IllegalArgumentException expected) {
		}

		// username can not be null
		try {
			userService.createUser("Herr", "Kasbah", "Ahmad", null, "password", adress);
			fail("IllegalArgumentException expected");
		} catch (IllegalArgumentException expected) {
		}

		// password can not be null
		try {
			userService.createUser("Herr", "Kasbah", "Ahmad", "AhmadK4", null, adress);
			fail("IllegalArgumentException expected");
		} catch (IllegalArgumentException expected) {
		}

		// honorifics correct?
		try {
			userService.createUser("NotHerr", "Kasbah", "Ahmad", "AhmadK4", "password", adress);
			fail("AnredeExc expected");
		} catch (HonorificsExc expected) {
		}

		// name correct?
		try {
			userService.createUser("Herr", "Kasbah0_443", "Ahmad", "AhmadK4", "password", adress);
			fail("NameExc expected");
		} catch (NameExc expected) {
		}

		// first name correct?
		try {
			userService.createUser("Herr", "Kasbah", "as  as", "AhmadK4", "password", adress);
			fail("NameExc expected");
		} catch (NameExc expected) {
		}

		// username correct?
		try {
			userService.createUser("Herr", "Kasbah", "Ahmad", "    a76", "password", adress);
			fail("NameExc expected");
		} catch (NameExc expected) {
		}

		// password correct?
		try {
			userService.createUser("Herr", "Kasbah", "Ahmad", "AhmadK4", "pass", adress);
			fail("PasswortExc expected");
		} catch (PasswordExc expected) {
		}

		// username already in use?
		try {
			userService.createUser("Herr", "Kasbah", "Ahmad", "AhmadK4", "password", adress);
			userService.createUser("Herr", "Khalil", "Ahmad", "AhmadK4", "password", adress);
			fail("UsernameExc expected");
		} catch (UsernameExc expected) {
		}
		//trying a correct user
		Address address = new Address(13347, "Berlin", "Ruheplatzstrasse", "23b");
		userService.createUser("Herr", "Koeller", "Ray", "ausername", "password", address);
	}

	@Test
	public void deleteUserExceptions() {
		try {
			userService.deleteUser(123457L);
			fail("UserNotInDatabaseExc expected");
		} catch (UserNotInDatabaseExc expected) {
		}
		try {
			userService.deleteUser(-123457L);
			fail("IllegalArgumentException expected");
		} catch (IllegalArgumentException expected) {
		}
		Address address = new Address(13347, "Berlin", "Ruheplatzstrasse", "23b");
		final User user=userService.createUser("Herr", "Koeller", "Ray", "ausername", "password", address);

		//trying a correct delete
		assertTrue(userService.findAllUsers().contains(user));		
		final Long userId = user.getId();
		userService.deleteUser(userId);
		
	}

	@Test
	public void findUserExceptions() {

		try {
			userService.findUser(-3L);
			fail("IllegalArgumentException expected");
		} catch (IllegalArgumentException expected) {
		}
		try {
			userService.findUser(123457L);
			fail("UserNotInDatabaseExc expected");
		} catch (UserNotInDatabaseExc expected) {
		}
		Address address = new Address(13347, "Berlin", "Ruheplatzstrasse", "23b");
		final User user=userService.createUser("Herr", "Koeller", "Ray", "ausername", "password", address);
		
		final User found = userService.findUser(user.getId());
		assertEquals(found, user);
		
	}

	@Test
	public void editUserExceptions() {

		final User user = userService.createUser("Frau", "Noerenberg", "Lena", "LenaUserName2", "password", adress);
		final Long id = user.getId();

		// editing user, but honorifics are null
		try {
			userService.editUser(id, null, "Hello", "Hello", "Hello", "password");
			fail("IllegalArgumentException expected");
		} catch (IllegalArgumentException expected) {
		}

		// editing user, but name is null
		try {
			userService.editUser(id, "Herr", null, "Hello", "Hello", "password");
			fail("IllegalArgumentException expected");
		} catch (IllegalArgumentException expected) {
		}

		// editing user, but first name is null
		try {
			userService.editUser(id, "Herr", "Hello", null, "Hello", "password");
			fail("IllegalArgumentException expected");
		} catch (IllegalArgumentException expected) {
		}
		;

		// editing user, but username is null
		try {
			userService.editUser(id, "Herr", "Hello", "Hello", null, "password");
			fail("IllegalArgumentException expected");
		} catch (IllegalArgumentException expected) {
		}
		;

		// editing user, but password is null
		try {
			userService.editUser(id, "Herr", "Hello", "Hello", "Hello", null);
			fail("IllegalArgumentException expected");
		} catch (IllegalArgumentException expected) {
		}

		// editing user, but with invalid honorifics
		try {
			userService.editUser(id, "notherr", "Hello", "Hello", "Hello", "password");
			fail("AnredeExc expected");
		} catch (HonorificsExc expected) {
		}

		// editing user, but with invalid name
		try {
			userService.editUser(id,"Herr", "C.L.A.R.A.", "Hello", "Hello", "password");
			fail("NameExc expected");
		} catch (NameExc expected) {
		}

		// editing user, but with invalid first name
		try {
			userService.editUser(id, "Herr", "Hello", "C.L.A.R.A.", "Hello", "password");
			fail("NameExc expected");
		} catch (NameExc expected) {
		}

		// editing user, but with invalid username
		try {
			userService.editUser(id, "Herr", "Hello", "Hello", "-----", "password");
			fail("NameExc expected");
		} catch (NameExc expected) {
		}

		// editing user, but with an already existing username
		userService.createUser("Herr", "Can", "Heilgermann", "Can65", "password", adress);
		try {
			userService.editUser(id, "Herr", "Hello", "Hello", "Can65", "password");
			fail("UsernameExc expected");
		} catch (UsernameExc expected) {
		}

		// editing user, but with invalid password
		try {
			userService.editUser(id, "Herr", "Hello", "Hello", "Hello", "pw");
			fail("PasswortExc expected");
		} catch (PasswordExc expected) {
		}
		// editing sucessfully
		final User user2 =userService.editUser(id, "Frau", "AName", "Firstname", "username", "password");
		assertEquals(user2.getHonorifics(), "Frau");
		assertEquals(user2.getName(), "AName");
		assertEquals(user2.getFirstname(), "Firstname");
		assertEquals(user2.getUsername(), "username");
		assertEquals(user2.getPassword(), "password");

	}

	@Test
	public void patchUserExceptions() {

		final User user = userService.createUser("Frau", "Noerenberg", "Lena", "LenaUserName2", "password", adress);
		final Long id = user.getId();

		// editing user, but with invalid honorifics
		try {
			userService.patchUser(id, "notherr", "Hello", "Hello", "Hello", "password");
			fail("AnredeExc expected");
		} catch (HonorificsExc expected) {
		}

		// editing user, but with invalid name
		try {
			userService.patchUser(id, "Herr", "C.L.A.R.A.", "Hello", "Hello", "password");
			fail("NameExc expected");
		} catch (NameExc expected) {
		}

		// editing user, but with invalid first name
		try {
			userService.patchUser(id, "Herr", "Hello", "C.L.A.R.A.", "Hello", "password");
			fail("NameExc expected");
		} catch (NameExc expected) {
		}

		// editing user, but with invalid username
		try {
			userService.patchUser(id, "Herr", "Hello", "Hello", "-----", "password");
			fail("NameExc expected");
		} catch (NameExc expected) {
		}

		// editing user, but with an already existing username
		userService.createUser("Herr", "Can", "Heilgermann", "Can65", "password", adress);
		try {
			userService.patchUser(id, "Herr", "Hello", "Hello", "Can65", "password");
			fail("UsernameExc expected");
		} catch (UsernameExc expected) {
		}

		// editing user, but with invalid password
		try {
			userService.patchUser(id,"Herr", "Hello", "Hello", "Hello", "pw");
			fail("PasswortExc expected");
		} catch (PasswordExc expected) {
		}
		
		// try patch with null
		try {
			userService.patchUser(id, null,null,null,null,null);
		} catch(Exception e) {
			fail("No Exception expected.");
		}
		//patching only password
		userService.patchUser(id, null, null, null, null, "password2");
		assertEquals(user.getPassword(), "password2");
	}

}

package de.beuth.clara.claraSoftware.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.beuth.clara.claraSoftware.domain.imports.UserRepository;

import static multex.MultexUtil.create;
/**
 * A Service for someone like the admin of this webapplication to handle everything surrounding the users.
 * @author Ahmad Kasbah
 */
@Service
public class UserService {

	// required repos
	final private UserRepository userRepo;
	
	@Autowired
	private transient OrderService orderService;

	/**
	 * Creates a UserService object.
	 * @param userRepo UserRepository - the required User Repository necessary 
	 * for managing all users 
	 */
	@Autowired
	public UserService(final UserRepository userRepo) {
		this.userRepo = userRepo;
	}

	/**
	 * Creates a User to this application.
	 * @param honorifics String - honorifics of this user
	 * @param name String - last name of this user
	 * @param firstname String - firstname of this user
	 * @param username String - username of this user
	 * @param password String - password of this user
	 * @param address Adress - adress of this user
	 * @return the created user
	 */
	public User createUser(final String honorifics, final String name, final String firstname, final String username,
		final String password, final Address address) {

		checkIfNull(honorifics, name, firstname, username, password);
		checkIfValid(honorifics, name, firstname, username, password);
		

		final User user = userRepo.save(new User(honorifics, name, firstname, username, password, address));
		user.createNewShoppingCart();
		
		return user;
	}

	/**
	 * Deletes a User associated with the given ID. All Orders including their order items must be 
	 * deleted as a consequence.
	 * @param id Long - ID of this user
	 */
	public void deleteUser(final Long id) {
		final User toDelete = checkForUserInDB(id);
		final List<Order> orders = toDelete.viewAllOrders();
		
		for(final Order order : orders) {
			orderService.delete(order.getId());
		}
		
		userRepo.delete(toDelete);
	}

	/**
	 * Finds a User associated with the given ID.
	 * @param id Long - ID of the user to be found
	 * @return the found user
	 */
	public User findUser(final Long id) {

		return checkForUserInDB(id);
	}

	/**
	 * Finds all Users of this application.
	 * @return a list of users
	 */
	public List<User> findAllUsers() {
		return userRepo.findAll();
	}

	/**
	 * Edits user information. Every argument must be valid for the editing to be successful.
	 * @param userId Long - ID of this user
	 * @param honorifics String - honorifics of this user
	 * @param name String - name of this user
	 * @param firstname String - first name of this user
	 * @param username String - username of this user
	 * @param password String - password of this user
	 * @return the edited user
	 */
	public User editUser(final Long userId, final String honorifics, final String name, final String firstname, final String username, final String password) {
		final User user1 = checkForUserInDB(userId);

		checkIfNull(honorifics, name, firstname, username, password);

		// testing if changes are valid
		checkIfValid(honorifics, name, firstname, username, password);
		user1.setHonorifics(honorifics);
		user1.setName(name);
		user1.setFirstname(firstname);
		user1.setUsername(username);
		user1.setPassword(password);

		return user1;
	}

	/**
	 * Modifies user information. Not every argument must be given (can be null!)
	 * @param userId Long - ID of this user
	 * @param honorifics String - honorifics of this user
	 * @param name String - name of this user
	 * @param firstname String - first name of this user
	 * @param username String - username of this user
	 * @param password String - password of this user
	 * @return the modified user
	 * @throws HonorificsExc if the parameter honorifics is neither Herr nor Frau
	 * @throws NameExc if the parameters name, firstname or username are empty or have
	 *             illegal characters
	 * @throws PasswordExc if password is too short
	 * @throws UsernameExc if username is already being used by another user
	 */
	public User patchUser(final Long userId, final String honorifics, final String name, final String firstname, final String username, final String password) {

		final User user1 = checkForUserInDB(userId);

		// testing what should be changed and testing if changes are valid
		if (honorifics != null) {
			if (!("Herr".equals(honorifics) || "Frau".equals(honorifics))) {
				throw create(HonorificsExc.class, honorifics);
			}
			user1.setHonorifics(honorifics); // change
		}

		if (name != null) {
			if (!(name.matches("[a-zA-Z]+"))) {
				throw create(NameExc.class, name);
			}
			user1.setName(name); // change
		}

		if (firstname != null) {
			if (!(firstname.matches("[a-zA-Z]+"))) {
				throw create(NameExc.class, firstname);
			}
			user1.setFirstname(firstname); // change
		}

		if (username != null) {
			if (!(username.matches("[a-zA-Z1-9]+")))
				throw create(NameExc.class, username);
			final List<User> allUsers = userRepo.findAll();
			for (final User user : allUsers) {
				if (user.getUsername().equals(username))
					throw create(UsernameExc.class, username);
			}
			user1.setUsername(username); // change
		}

		if (password != null) {
			if (password.length() < 6)
				throw create(PasswordExc.class, password);
			user1.setPassword(password); // change
		}

		return user1;
	}

	// -------------------------------------------------------
	// Exceptions

	/** User {0} is not in database. */
	@SuppressWarnings("serial")
	public static class UserNotInDatabaseExc extends multex.Exc {
	}

	/** Illegal honorific {0}. Valid are: Herr and Frau. */
	@SuppressWarnings("serial")
	public static class HonorificsExc extends multex.Exc {
	}

	/** Illegal (first) name {0}. An invalid character was detected. */
	@SuppressWarnings("serial")
	public static class NameExc extends multex.Exc {
	}

	/** Username {0} already in use. Choose a different one. */
	@SuppressWarnings("serial")
	public static class UsernameExc extends multex.Exc {
	}

	/** Password {0} is too short */
	@SuppressWarnings("serial")
	public static class PasswordExc extends multex.Exc {
	}




	// ---------------------------------------------------------

	/**
	 * Checks if the parameters are null
	 * 
	 * @param honorifics String - Honorifics of user
	 * @param name String - Name of the user
	 * @param firstname String - First name of the user
	 * @param username String - Username of the user
	 * @param password String - the user password
	 * @throws IllegalArgumentException if one of the parameters is null
	 */
	private void checkIfNull(final String honorifics, final String name, final String firstname, final String username, final String password) {
		if (honorifics == null)
			throw new IllegalArgumentException("honorific is null");
		if (name == null)
			throw new IllegalArgumentException("name is null");
		if (firstname == null)
			throw new IllegalArgumentException("first name is null");
		if (username == null)
			throw new IllegalArgumentException("username is null");
		if (password == null)
			throw new IllegalArgumentException("password is null");
	}

	/**
	 * Validates the parameters on illegal characters, correctness and/or
	 * appropriate length
	 * 
	 * @param honorifics String - Honorifics of user
	 * @param name String - Name of the user
	 * @param firstname String - First name of the user
	 * @param username String - Username of the user
	 * @param password String - the user password
	 * @throws HonorificsExc if the parameter honorifics is neither Herr nor Frau
	 * @throws NameExc if the parameters name, firstname or username are empty or have
	 *             illegal characters
	 * @throws PasswordExc if password is too short
	 * @throws UsernameExc if username is already being used by another user
	 */
	private void checkIfValid(final String honorifics, final String name, final String firstname, final String username, final String password) {
		if (!("Herr".equals(honorifics) || "Frau".equals(honorifics))) {
			throw create(HonorificsExc.class, honorifics);
		}
		if (!(firstname.matches("[a-zA-Z]+"))) {
			throw create(NameExc.class, firstname);
		}
		if (!(name.matches("[a-zA-Z]+"))) {
			throw create(NameExc.class, name);
		}
		if (password.length() < 6)
			throw create(PasswordExc.class, password);

		if (!(username.matches("[a-zA-Z0-9]+")))
			throw create(NameExc.class, username);
		// check if username is already in use
		final List<User> allUsers = userRepo.findAll();
		for (final User user : allUsers) {
			if (user.getUsername().equals(username))
				throw create(UsernameExc.class, username);
		}
	}

	/**
	 * Checks if the User is in database
	 * 
	 * @param id Long - the ID of the user to be found
	 * @return User the found User
	 * @throws IllegalArgumentException if ID is negative
	 * @throws UserNotInDatabaseExc if User is not in database
	 */
	private User checkForUserInDB(final Long id) {
		// negative ID
		if (id < 1)
			throw new IllegalArgumentException("ID can not be negative.");
		final Optional<User> oUser = userRepo.find(id);
		if (!oUser.isPresent())
			throw create(UserNotInDatabaseExc.class, id);
		return oUser.get();
	}
}

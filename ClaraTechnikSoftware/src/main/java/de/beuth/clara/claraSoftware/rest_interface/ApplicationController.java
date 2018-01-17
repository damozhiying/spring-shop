package de.beuth.clara.claraSoftware.rest_interface;


import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import de.beuth.clara.claraSoftware.domain.Bankaccount;
import de.beuth.clara.claraSoftware.domain.Order;
import de.beuth.clara.claraSoftware.domain.OrderItem;
import de.beuth.clara.claraSoftware.domain.OrderService;
import de.beuth.clara.claraSoftware.domain.User;
import de.beuth.clara.claraSoftware.domain.Address;
import de.beuth.clara.claraSoftware.domain.Amount;
import de.beuth.clara.claraSoftware.domain.Article;
import de.beuth.clara.claraSoftware.domain.UserService;
import de.beuth.clara.claraSoftware.domain.ArticleService;

/**
 * Controller class for CRUD-Operations as a REST-Service.
 * @author Ray Koeller (Co-Authors: Lena Noerenberg, Ahmad Khalil, Can Heilgermann, Ahmad Kasbah)
 */
@Transactional
@RestController
public class ApplicationController {

	private final UserService userService;
	private final ArticleService articleService;
	private final OrderService orderService;

	@Autowired
	public ApplicationController(final UserService userService, final ArticleService articleService, final OrderService orderService) {
		this.userService = userService;
		this.articleService= articleService;
		this.orderService = orderService;
	}
	
	
	
	/* -------- generating standard articles and users  (USE ONLY ONCE) --------- */ 
	@PostMapping("admin/dummydata")
	public ResponseEntity<String> createDefaults(){
		System.out.println("Application Controller POST /dummydata");
		if(userService.findAllUsers().isEmpty() && articleService.findAllArticles().isEmpty()) {
			final Address address = new Address(13347, "Berlin", "Ruheplatz", "12b");
			final List<String> usernames = new WebSecurityConfig().getPredefinedUsernames();
			final String password = new WebSecurityConfig().getPassword();

			@SuppressWarnings("unused")
			final User admin = userService.createUser("Herr", "Clara", "Clara", "claradmin", "password", address);
			
			final User can =userService.createUser("Herr", "Can", "Heilgermann", usernames.get(0), password, address);
			can.addBankaccount("DE23456782345678", "BELADEBEXXX");
			can.addBankaccount("DE23456782345679", "BELADEBEXXX");
			final User lena =userService.createUser("Frau", "Lena", "Noerenberg", usernames.get(1), password, address);
			lena.addBankaccount("DE2233782345678", "DRESDEFF100");
			final User ahmad1 =userService.createUser("Herr", "Ahmad", "Khalil", usernames.get(2), password, address);
			ahmad1.addBankaccount("DE987654875671", "SOMEBANK200");
			final User ray =userService.createUser("Herr", "Ray", "Koeller", usernames.get(3), password, address);
			ray.addBankaccount("DE10622345678910", "BELADEBEXXX");
			final User ahmad2 =userService.createUser("Herr", "Ahmad", "Kasbah", usernames.get(4), password, address);
			ahmad2.addBankaccount("DE12345678910", "DRESDEFF200");
			
			articleService.createArticle("GPU- GTX 1080 TI", "A very strong GPU build by Nvidia.", new Amount(900));
			articleService.createArticle("GPU- GTX 1070", "A strong GPU build by Nvidia.", new Amount(720));
			articleService.createArticle("i7-Core", "i7-Quad-Core with 2.7GHz.", new Amount(350));
			articleService.createArticle("Asus Laptop 15 inch", "A very strong laptop build by Asus.", new Amount(400));
			articleService.createArticle("Microsoft Surface", "Ultrabook from Microsoft. Including Touchpen and Keyboard.", new Amount(1600));
			articleService.createArticle("Apple Macbook Pro 15", "Apple Ultrabook with Touchbar.", new Amount(2200));
			articleService.createArticle("Razer Kraken USB", "Headset from Razer with green Backlighting.", new Amount(70));
			articleService.createArticle("Razer Keyboard", "Mechanical Keybaord with RGB Backlighting.", new Amount(99));
			articleService.createArticle("Samsung Galaxy S8", "New FLagship from Samsung with Octa-Core-Processor.", new Amount(600));
			articleService.createArticle("HDMI Cable", "A very strong HDMI cable build by super speed.", new Amount(20));
		}
		return new ResponseEntity<>(HttpStatus.CREATED);
	}
	
	
	/*---------User----------*/
	@PostMapping("/users")
	public ResponseEntity<UserResource> createUser(@RequestBody final UserResource userResource) {
		System.out.println("ApplicationController POST /users");
		final User user1 = userService.createUser(userResource.honorifics, userResource.name, userResource.firstname,
				userResource.username, userResource.password, userResource.address);
		return new ResponseEntity<>(new UserResource(user1), HttpStatus.CREATED);
	}

	@DeleteMapping("/admin/users/{userId}")
	public ResponseEntity<String> deleteClient(@PathVariable final Long userId) {
		System.out.println("ApplicationController DELETE /users/" + userId);
		userService.deleteUser(userId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@GetMapping(path = "/users")
	public ResponseEntity<UserResource[]> findUsers() {
		System.out.println("ApplicationController GET /users");
		final List<User> users = userService.findAllUsers();
		return _usersToResources(users);
	}

	@GetMapping(path = "/users/{userId}")
	public ResponseEntity<UserResource> findUser(@PathVariable final Long userId) {
		System.out.println("ApplicationController GET /users/" + userId);
		final User user1 = userService.findUser(userId);
		return new ResponseEntity<>(new UserResource(user1), HttpStatus.OK);
	}

	@PutMapping(path = "/users/{userId}")
	public ResponseEntity<UserResource> editUser(@PathVariable final Long userId,
			@RequestBody final UserResource userResource) {
		System.out.println("ApplicationController PUT /users/" + userId);
		final User user1 = userService.editUser(userId, userResource.honorifics,userResource.name, userResource.firstname,userResource.username,userResource.password);
		return new ResponseEntity<>(new UserResource(user1), HttpStatus.OK);
	}

	@PatchMapping(path = "/users/{userId}")
	public ResponseEntity<UserResource> patchUser(@PathVariable final Long userId,
			@RequestBody final UserResource userResource) {
		System.out.println("ApplicationController PATCH /users/" + userId);
		final User user1 = userService.patchUser(userId, userResource.honorifics,userResource.name,userResource.firstname,userResource.username,userResource.password);
		return new ResponseEntity<>(new UserResource(user1), HttpStatus.OK);
	}

	/* --------- bankaccounts --------- */
	@PostMapping(path = "/users/{userId}/bankaccounts")
	public ResponseEntity<Bankaccount> createBankaccount(@PathVariable final Long userId, @RequestBody final Bankaccount bankaccount){
		System.out.println("ApplicationController POST /users/" + userId +"/bankaccounts");
		final User user1 = userService.findUser(userId);
		user1.addBankaccount(bankaccount.getIban(), bankaccount.getBic());
		final List<Bankaccount> bankaccounts = user1.getBankaccounts();
		return new ResponseEntity<>(bankaccounts.get(bankaccounts.size()-1), HttpStatus.CREATED);
	}
	
	@GetMapping(path = "/users/{userId}/bankaccounts")
	public ResponseEntity<BankaccountResource[]> getBankaccounts(@PathVariable final Long userId){
		System.out.println("ApplicationController GET /users/" + userId +"/bankaccounts");
		final List<Bankaccount> bankaccounts = userService.findUser(userId).getBankaccounts();
		return _bankaccountsToResources(userId,bankaccounts);	
	}
	
	@DeleteMapping(path = "/users/{userId}/bankaccounts/{bankaccountnumber}")
	public ResponseEntity<String> deleteBankaccount(@PathVariable final Long userId, @PathVariable int bankaccountnumber ){
		System.out.println("ApplicationController DELETE /users/" + userId +"/bankaccounts" + bankaccountnumber);
		final User user1 = userService.findUser(userId);
		user1.deleteBankaccount(bankaccountnumber);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);	
	}
	
	@DeleteMapping(path = "/users/{userId}/bankaccounts")
	public ResponseEntity<String> deleteAllBankaccountsOfUser(@PathVariable final Long userId){
		System.out.println("ApplicationController DELETE /users/" + userId +"/bankaccounts");
		final User user1 = userService.findUser(userId);
		user1.deleteAllBankaccounts();
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	/* ----------- address ----------- */
	
	@PutMapping(path = "/users/{userId}/address")
	public ResponseEntity<Address> createAdress(@PathVariable final Long userId, @RequestBody final Address address){
		System.out.println("ApplicationController PUT /users/" + userId +"/address");
		final User user1 = userService.findUser(userId);
		user1.setAdress(address);
		return new ResponseEntity<>(user1.getAdress(), HttpStatus.CREATED);
	}
	
	@GetMapping(path = "/users/{userId}/address")
	public ResponseEntity<Address> getAdress(@PathVariable final Long userId){
		System.out.println("ApplicationController GET /users/" + userId +"/address");
		final User user1 = userService.findUser(userId);
		return new ResponseEntity<>(user1.getAdress(), HttpStatus.OK);
	}
	//-------------------------------------------------------
	/* -------- Article ----------- */
	@PostMapping("admin/articles")
	public ResponseEntity<ArticleResource> createArticle(@RequestBody final ArticleResource articleResource) {
		System.out.println("ApplicationController POST /articles");
		final Article article1 = articleService.createArticle(articleResource.name, articleResource.description,
				articleResource.price);
		return new ResponseEntity<>(new ArticleResource(article1), HttpStatus.CREATED);
	}

	@DeleteMapping("admin/articles/{articleId}")
	public ResponseEntity<String> deleteArticle(@PathVariable final Long articleId) {
		System.out.println("ApplicationController DELETE /articles/"+articleId);
		articleService.deleteArticle(articleId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@DeleteMapping("admin/articles")
	public ResponseEntity<String> deleteAllArticles(){
		System.out.println("ApplicationController DELETE /articles");
		articleService.deleteAll();
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	@GetMapping(path = "/articles")
	public ResponseEntity<ArticleResource[]> findArticles() {
		System.out.println("ApplicationController GET /articles");
		final List<Article> articles = articleService.findAllArticles();
		return _articlesToResources(articles);
	}

	@GetMapping(path = "/articles/{articleId}")
	public ResponseEntity<ArticleResource> findArticle(@PathVariable final Long articleId) {
		System.out.println("ApplicationController GET /articles/" + articleId);
		final Article article1 = articleService.findArticle(articleId); 
		return new ResponseEntity<>(new ArticleResource(article1), HttpStatus.OK);
	}

	@PutMapping(path = "admin/articles/{articleId}")
	public ResponseEntity<ArticleResource> editArticle(@PathVariable final Long articleId,
			@RequestBody final ArticleResource articleResource) {
		System.out.println("ApplicationController PUT /articles" + articleId);
		final Article article1 = articleService.editArticle(articleId, articleResource.name, articleResource.description, articleResource.price);
		return new ResponseEntity<>(new ArticleResource(article1), HttpStatus.OK);
	}

	@PatchMapping(path = "admin/articles/{articleId}")
	public ResponseEntity<ArticleResource> patchArticle(@PathVariable final Long articleId,
			@RequestBody final ArticleResource articleResource) {
		System.out.println("ApplicationController PATCH /articles" + articleId);
		final Article article1 = articleService.patchArticle(articleId, articleResource.name, articleResource.description, articleResource.price);
		return new ResponseEntity<>(new ArticleResource(article1), HttpStatus.OK);
	}
	
	@GetMapping(path ="users/{userId}/articles/{keyword}")
	public ResponseEntity<ArticleResource[]> searchAllArticles(@PathVariable final Long userId,@PathVariable final String keyword){
		System.out.println("ApplicationController GET users/" + userId + "/articles/" + keyword);
		final User user1 = userService.findUser(userId);
		return _articlesToResources(user1.searchArticles(keyword));
	}
	
	//-------------------------------------------------------
	/* -------- Order ----------- */
	
	@GetMapping(path = "admin/orders")
	public ResponseEntity<OrderResource[]> findOrders(){
		System.out.println("ApplicationController GET /orders/");
		final List<Order> order1 = orderService.findAllOrders();
		return _ordersToResources(order1);
	}
	
	@GetMapping(path = "admin/orders/{orderId}")
	public ResponseEntity<OrderResource> findOrder(@PathVariable final Long orderId){
		System.out.println("ApplicationController GET /orders/"+ orderId);
		final Order order1 = orderService.findOrder(orderId);
		return new ResponseEntity<>(new OrderResource(order1), HttpStatus.OK);
	}

	
	// Get all orders of a specific user
	@GetMapping("users/{userId}/orders")
	public ResponseEntity<OrderResource[]> findOrdersOfUser(@PathVariable final Long userId){
		System.out.println("ApplicationController GET /orders/"+ userId + "/orders");
		final User user1 = userService.findUser(userId);
		final List<Order> order1 = orderService.findAllOrdersOfUser(user1);
		return _ordersToResources(order1);
	}
	
	// user deleting their own orders
	@DeleteMapping("users/{userId}/orders/{orderId}")
	public ResponseEntity<String> deleteOrderOfUser(@PathVariable final Long userId,@PathVariable final Long orderId) {
		System.out.println("ApplicationController DELETE users/"+ userId + "/orders/" + orderId);
		final User user1 = userService.findUser(userId);
		user1.cancelOrder(orderId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	// user placing their order
	@PostMapping("users/{userId}/orders/{orderId}")
	public ResponseEntity<OrderResource> placeOrder(@PathVariable final Long userId, @PathVariable final Long orderId){
		System.out.println("ApplicationController POST users/"+ userId + "/orders/" + orderId);
		final User user1 = userService.findUser(userId);
		final Order order1 = user1.order(orderId);
		return new ResponseEntity<>(new OrderResource(order1), HttpStatus.CREATED);
	}
	
	
	//-------------------------------------------------------
	/* -------- Orderitem ----------- */
	
	@GetMapping("admin/orders/{orderId}/orderitems")
	public ResponseEntity<OrderItemResource[]> findOrderItemsOfOrder(@PathVariable final Long orderId){
		System.out.println("ApplicationController GET /orders/" +  orderId + "/orderitems" );
		final Order order1 = orderService.findOrder(orderId);
		return _orderItemsToResources(order1.getOrderItems());
	}
	
	@GetMapping("users/{userId}/shoppingcart")
	public ResponseEntity<OrderItemResource[]> findShoppingCart(@PathVariable final Long userId){
		System.out.println("ApplicationController GET /users/" +  userId + "/shoppingCart" );
		final User user1 = userService.findUser(userId);
		return _orderItemsToResources(user1.viewShoppingCart());
	}
	
	@PostMapping("users/{userId}/orders/{orderId}/add/{articleId}")
	public ResponseEntity<OrderItemResource> putIntoOrder(@PathVariable final Long userId,
			@PathVariable final Long orderId,@PathVariable final Long articleId, @RequestBody OrderItemResource orderItemResource){
		System.out.println("ApplicationController POST /users/"+ userId +"/orders/" + orderId +"/add/"+ articleId);
		final User user1 = userService.findUser(userId);
		final OrderItem orderItem =user1.addToShoppingCart(orderId, articleId, orderItemResource.articleAmount);
		
		return new ResponseEntity<>(new OrderItemResource(orderItem), HttpStatus.CREATED);
	}
	
	@PostMapping("users/{userId}/orders/{orderId}/remove/{articleId}")
	public ResponseEntity<String> deleteFromOrder(@PathVariable final Long userId,
			@PathVariable final Long orderId,@PathVariable final Long articleId, @RequestBody OrderItemResource orderItemResource){
		System.out.println("ApplicationController POST /users/"+ userId +"/orders/" + orderId +"/remove"+ articleId);
		final User user1 = userService.findUser(userId);
		user1.removeFromShoppingCart(orderId, articleId, orderItemResource.articleAmount);
		
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	@GetMapping("users/{userId}/orders/{orderId}/orderitems")
	public ResponseEntity<OrderItemResource[]> findOrderItemsOfOrderFromUser(@PathVariable final Long userId,@PathVariable final Long orderId){
		System.out.println("ApplicationController GET users/" + userId +"/orders/" +  orderId + "/orderitems" );
		final User user1 = userService.findUser(userId);
		return _orderItemsToResources(user1.viewOrder(orderId));
	}
	
	//-------------------------------------------------------
	/*------- Mapper-Methods --------*/
	/**
	 * Maps a List of Users into an Array of UserResources.
	 * @param users - the list of users
	 * @return the ResponseEntity containing the UserResource Array and the Http Status
	 */
	private ResponseEntity<UserResource[]> _usersToResources(final List<User> users) {
		final Stream<UserResource> result = users.stream().map(c -> new UserResource(c));
		final UserResource[] resultArray = result.toArray(size -> new UserResource[size]);
		return new ResponseEntity<>(resultArray, HttpStatus.OK);
	}
	
	/**
	 * Maps a List of Bankaccounts into an Array of BankaccountResources.
	 * @param userId - the ID of the user and the bankaccounts
	 * @param bankaccounts - the list of Bankaccounts
	 * @return the ResponseEntity containing the BankaccountResource Array and the Http Status
	 */
	private ResponseEntity<BankaccountResource[]> _bankaccountsToResources(final Long userId,final List<Bankaccount> bankaccounts) {
		final Stream<BankaccountResource> result = bankaccounts.stream().map(c -> new BankaccountResource(userId,c));
		final BankaccountResource[] resultArray = result.toArray(size -> new BankaccountResource[size]);
		return new ResponseEntity<>(resultArray, HttpStatus.OK);
	}
	
	/**
	 * Maps a List of Articles into an Array of ArticleResources.
	 * @param articles - the list of Articles
	 * @return the ResponseEntity containing the ArticleResource Array and the Http Status
	 */
	private ResponseEntity<ArticleResource[]> _articlesToResources(final List<Article> articles) {
		final Stream<ArticleResource> result = articles.stream().map(c -> new ArticleResource(c));
		final ArticleResource[] resultArray = result.toArray(size -> new ArticleResource[size]);
		return new ResponseEntity<>(resultArray, HttpStatus.OK);
	}
	
	/**
	 * Maps a List of Orders into an Array of OrderResources.
	 * @param orders - the list of orders
	 * @return the ResponseEntity containing the OrderResource Array and the Http Status
	 */
	private ResponseEntity<OrderResource[]> _ordersToResources(final List<Order> orders) {
		final Stream<OrderResource> result = orders.stream().map(c -> new OrderResource(c));
		final OrderResource[] resultArray = result.toArray(size -> new OrderResource[size]);
		return new ResponseEntity<>(resultArray, HttpStatus.OK);
	}
	
	/**
	 * Maps a List of OrderItems into an Array of OrderResources.
	 * @param orders - the list of orderItems
	 * @return the ResponseEntity containing the OrderItemResource Array and the Http Status
	 */
	private ResponseEntity<OrderItemResource[]> _orderItemsToResources(final List<OrderItem> orders) {
		final Stream<OrderItemResource> result = orders.stream().map(c -> new OrderItemResource(c));
		final OrderItemResource[] resultArray = result.toArray(size -> new OrderItemResource[size]);
		return new ResponseEntity<>(resultArray, HttpStatus.OK);
	}


}

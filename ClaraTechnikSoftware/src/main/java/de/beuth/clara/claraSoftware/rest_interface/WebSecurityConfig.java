package de.beuth.clara.claraSoftware.rest_interface;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.InMemoryUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * A class configuring websecurity. (with predefined users)
 * @author Ray Koeller
 *
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	private final String ADMIN = "ADMIN";
	private final String USER = "USER";
	private final String password = "password";
	private final List<String> predefinedUsernames = Arrays.asList("can1234", "lena1234", "ahmad1234",
			"ray1234", "ahmad123");
	
	@Autowired
	protected void configureGlobal(final AuthenticationManagerBuilder auth) throws Exception {
		final InMemoryUserDetailsManagerConfigurer<AuthenticationManagerBuilder> inMemoryAuthentication = auth
				.inMemoryAuthentication();
		inMemoryAuthentication.withUser("claradmin").password(password).roles(USER, ADMIN);
		for (final String username : predefinedUsernames) {
			inMemoryAuthentication.withUser(username).password(password).roles(USER);
		}
	}


	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http
			.authorizeRequests()
			.antMatchers("/", "/dummydata").permitAll()
			.antMatchers("/users/**").hasRole(USER)
			.antMatchers("/admin/**").hasRole(ADMIN).
			anyRequest().authenticated()
			.and().httpBasic()
			.and().csrf().disable();
	}

	public List<String> getPredefinedUsernames() {
		return predefinedUsernames;
	}
	
	public String getPassword() {
		return password;
	}
}

// config/SecurityConfig.java
package com.bookstore.backend.config;

import com.bookstore.backend.service.UserService;
import com.bookstore.backend.util.JwtAuthenticationEntryPoint;
import com.bookstore.backend.util.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserService userService;

	@Autowired
	private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter() {
		return new JwtAuthenticationFilter();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userService).passwordEncoder(passwordEncoder());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.cors().and().csrf().disable().exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint)
				.and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
				.authorizeRequests()
				// Public endpoints
				.antMatchers("/api/auth/**").permitAll().antMatchers(HttpMethod.GET, "/api/books/**").permitAll()
				.antMatchers(HttpMethod.GET, "/api/categories/**").permitAll().antMatchers("/api/payments/webhook")
				.permitAll().antMatchers("/uploads/**").permitAll()

				// Admin only endpoints
				.antMatchers("/api/admin/**").hasRole("ADMIN").antMatchers(HttpMethod.POST, "/api/books")
				.hasRole("ADMIN").antMatchers(HttpMethod.PUT, "/api/books/**").hasRole("ADMIN")
				.antMatchers(HttpMethod.DELETE, "/api/books/**").hasRole("ADMIN")
				.antMatchers(HttpMethod.POST, "/api/categories").hasRole("ADMIN")
				.antMatchers(HttpMethod.PUT, "/api/categories/**").hasRole("ADMIN")
				.antMatchers(HttpMethod.DELETE, "/api/categories/**").hasRole("ADMIN")

				// User endpoints
				.antMatchers("/api/cart/**").hasAnyRole("USER", "ADMIN").antMatchers("/api/wishlist/**")
				.hasAnyRole("USER", "ADMIN").antMatchers("/api/orders/**").hasAnyRole("USER", "ADMIN")
				.antMatchers("/api/payments/**").hasAnyRole("USER", "ADMIN").antMatchers("/api/users/profile")
				.hasAnyRole("USER", "ADMIN")

				.anyRequest().authenticated();

		http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
	}
}
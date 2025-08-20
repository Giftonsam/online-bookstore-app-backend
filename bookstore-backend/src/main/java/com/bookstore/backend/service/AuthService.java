// service/AuthService.java
package com.bookstore.backend.service;

import com.bookstore.backend.entity.User;
import com.bookstore.backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserService userService;

	@Autowired
	private JwtUtil jwtUtil;

	public Map<String, Object> login(String email, String password) {
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(email, password));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtil.generateJwtToken(authentication);

		User user = userService.getUserByEmail(email);

		Map<String, Object> response = new HashMap<>();
		response.put("token", jwt);
		response.put("type", "Bearer");
		response.put("user", user);

		return response;
	}

	public User register(User user) {
		return userService.createUser(user);
	}

	public User getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String email = authentication.getName();
		return userService.getUserByEmail(email);
	}

	public UserService getUserService() {
		return userService;
	}
}
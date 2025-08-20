// controller/AuthController.java
package com.bookstore.backend.controller;

import com.bookstore.backend.dto.AuthDto;
import com.bookstore.backend.entity.User;
import com.bookstore.backend.service.AuthService;
import com.bookstore.backend.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

	@Autowired
	private AuthService authService;

	@Autowired
	private EmailService emailService;

	@PostMapping("/login")
	public ResponseEntity<?> login(@Valid @RequestBody AuthDto.LoginRequest request) {
		try {
			Map<String, Object> response = authService.login(request.getEmail(), request.getPassword());
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new AuthDto.MessageResponse("Error: Invalid email or password!"));
		}
	}

	@PostMapping("/register")
	public ResponseEntity<?> register(@Valid @RequestBody AuthDto.RegisterRequest request) {
		try {
			if (authService.getUserService().existsByEmail(request.getEmail())) {
				return ResponseEntity.badRequest().body(new AuthDto.MessageResponse("Error: Email is already taken!"));
			}

			User user = new User();
			user.setFirstName(request.getFirstName());
			user.setLastName(request.getLastName());
			user.setEmail(request.getEmail());
			user.setPassword(request.getPassword());
			user.setPhone(request.getPhone());
			user.setAddress(request.getAddress());

			User savedUser = authService.register(user);

			// Send welcome email
			emailService.sendWelcomeEmail(savedUser);

			return ResponseEntity.ok(new AuthDto.MessageResponse("User registered successfully!"));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new AuthDto.MessageResponse("Error: " + e.getMessage()));
		}
	}

	@GetMapping("/me")
	public ResponseEntity<?> getCurrentUser() {
		try {
			User user = authService.getCurrentUser();
			return ResponseEntity.ok(user);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new AuthDto.MessageResponse("Error: " + e.getMessage()));
		}
	}
}
// controller/UserController.java
package com.bookstore.backend.controller;

import com.bookstore.backend.dto.UserDto;
import com.bookstore.backend.entity.User;
import com.bookstore.backend.service.AuthService;
import com.bookstore.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private AuthService authService;

	@GetMapping("/profile")
	public ResponseEntity<User> getUserProfile() {
		User user = authService.getCurrentUser();
		return ResponseEntity.ok(user);
	}

	@PutMapping("/profile")
	public ResponseEntity<User> updateUserProfile(@Valid @RequestBody UserDto.UpdateProfileRequest request) {
		Long userId = authService.getCurrentUser().getId();

		User user = new User();
		user.setFirstName(request.getFirstName());
		user.setLastName(request.getLastName());
		user.setPhone(request.getPhone());
		user.setAddress(request.getAddress());

		User updatedUser = userService.updateUser(userId, user);
		return ResponseEntity.ok(updatedUser);
	}

	@GetMapping("/admin/all")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<User>> getAllUsers() {
		List<User> users = userService.getAllUsers();
		return ResponseEntity.ok(users);
	}

	@GetMapping("/admin/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<User> getUserById(@PathVariable Long id) {
		User user = userService.getUserById(id);
		return ResponseEntity.ok(user);
	}

	@DeleteMapping("/admin/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> deleteUser(@PathVariable Long id) {
		userService.deleteUser(id);
		return ResponseEntity.ok(new UserDto.MessageResponse("User deleted successfully!"));
	}

	@GetMapping("/admin/search")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<User>> searchUsers(@RequestParam String keyword) {
		List<User> users = userService.searchUsers(keyword);
		return ResponseEntity.ok(users);
	}
}
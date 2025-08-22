// service/UserService.java
package com.bookstore.backend.service;

import com.bookstore.backend.entity.User;
import com.bookstore.backend.entity.Cart;
import com.bookstore.backend.entity.Wishlist;
import com.bookstore.backend.repository.UserRepository;
import com.bookstore.backend.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

		return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
				user.getIsActive(), true, true, true, authorities);
	}

	public User createUser(User user) {
		if (userRepository.existsByEmail(user.getEmail())) {
			throw new RuntimeException("Email is already taken!");
		}

		user.setPassword(passwordEncoder.encode(user.getPassword()));
		User savedUser = userRepository.save(user);

		// Create cart and wishlist for new user
		Cart cart = new Cart(savedUser);
		savedUser.setCart(cart);

		Wishlist wishlist = new Wishlist(savedUser);
		savedUser.setWishlist(wishlist);

		return userRepository.save(savedUser);
	}

	public User getUserById(Long id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
	}

	public User getUserByEmail(String email) {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
	}

	public List<User> getAllUsers() {
		return userRepository.findByIsActiveTrue();
	}

	public User updateUser(Long id, User userDetails) {
		User user = getUserById(id);

		user.setFirstName(userDetails.getFirstName());
		user.setLastName(userDetails.getLastName());
		user.setPhone(userDetails.getPhone());
		user.setAddress(userDetails.getAddress());

		return userRepository.save(user);
	}

	public void deleteUser(Long id) {
		User user = getUserById(id);
		user.setIsActive(false);
		userRepository.save(user);
	}

	public boolean existsByEmail(String email) {
		return userRepository.existsByEmail(email);
	}

	public List<User> searchUsers(String keyword) {
		return userRepository.searchUsers(keyword);
	}

	public Long countActiveUsers() {
		return userRepository.countActiveUsersByRole(User.Role.USER);
	}

	public Optional<User> findById(Long id) {
		return userRepository.findById(id);
	}
}
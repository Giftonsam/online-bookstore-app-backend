// Cart Service
// service/CartService.java
package com.bookstore.backend.service;

import com.bookstore.backend.entity.Cart;
import com.bookstore.backend.entity.CartItem;
import com.bookstore.backend.entity.Book;
import com.bookstore.backend.entity.User;
import com.bookstore.backend.repository.CartRepository;
import com.bookstore.backend.repository.CartItemRepository;
import com.bookstore.backend.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class CartService {

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private CartItemRepository cartItemRepository;

	@Autowired
	private BookService bookService;

	@Autowired
	private UserService userService;

	public Cart getCartByUserId(Long userId) {
		return cartRepository.findByUserIdWithItems(userId).orElseGet(() -> {
			User user = userService.getUserById(userId);
			Cart cart = new Cart(user);
			return cartRepository.save(cart);
		});
	}

	public CartItem addItemToCart(Long userId, Long bookId, Integer quantity) {
		Cart cart = getCartByUserId(userId);
		Book book = bookService.getBookById(bookId);

		// Check if book is already in cart
		Optional<CartItem> existingItem = cartItemRepository.findByCartAndBook(cart, book);

		if (existingItem.isPresent()) {
			// Update quantity if item already exists
			CartItem cartItem = existingItem.get();
			cartItem.setQuantity(cartItem.getQuantity() + quantity);
			return cartItemRepository.save(cartItem);
		} else {
			// Add new item to cart
			CartItem cartItem = new CartItem(cart, book, quantity);
			return cartItemRepository.save(cartItem);
		}
	}

	public CartItem updateCartItem(Long userId, Long bookId, Integer quantity) {
		Cart cart = getCartByUserId(userId);
		Book book = bookService.getBookById(bookId);

		CartItem cartItem = cartItemRepository.findByCartAndBook(cart, book)
				.orElseThrow(() -> new ResourceNotFoundException("Item not found in cart"));

		if (quantity <= 0) {
			cartItemRepository.delete(cartItem);
			return null;
		} else {
			cartItem.setQuantity(quantity);
			return cartItemRepository.save(cartItem);
		}
	}

	public void removeItemFromCart(Long userId, Long bookId) {
		Cart cart = getCartByUserId(userId);
		Book book = bookService.getBookById(bookId);

		CartItem cartItem = cartItemRepository.findByCartAndBook(cart, book)
				.orElseThrow(() -> new ResourceNotFoundException("Item not found in cart"));

		cartItemRepository.delete(cartItem);
	}

	public void clearCart(Long userId) {
		Cart cart = getCartByUserId(userId);
		cartItemRepository.deleteAllByCartId(cart.getId());
	}

	public Integer getCartItemCount(Long userId) {
		return cartItemRepository.countItemsByUserId(userId);
	}
}
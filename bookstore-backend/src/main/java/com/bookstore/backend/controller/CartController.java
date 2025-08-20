// controller/CartController.java
package com.bookstore.backend.controller;

import com.bookstore.backend.dto.CartItemDto;
import com.bookstore.backend.entity.Cart;
import com.bookstore.backend.entity.CartItem;
import com.bookstore.backend.service.AuthService;
import com.bookstore.backend.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CartController {

	@Autowired
	private CartService cartService;

	@Autowired
	private AuthService authService;

	@GetMapping
	public ResponseEntity<Cart> getCart() {
		Long userId = authService.getCurrentUser().getId();
		Cart cart = cartService.getCartByUserId(userId);
		return ResponseEntity.ok(cart);
	}

	@PostMapping("/add")
	public ResponseEntity<CartItem> addItemToCart(@Valid @RequestBody CartItemDto.AddToCartRequest request) {
		Long userId = authService.getCurrentUser().getId();
		CartItem cartItem = cartService.addItemToCart(userId, request.getBookId(), request.getQuantity());
		return ResponseEntity.ok(cartItem);
	}

	@PutMapping("/update")
	public ResponseEntity<?> updateCartItem(@Valid @RequestBody CartItemDto.UpdateCartItemRequest request) {
		Long userId = authService.getCurrentUser().getId();
		CartItem cartItem = cartService.updateCartItem(userId, request.getBookId(), request.getQuantity());

		if (cartItem == null) {
			return ResponseEntity.ok(new CartItemDto.MessageResponse("Item removed from cart"));
		}
		return ResponseEntity.ok(cartItem);
	}

	@DeleteMapping("/remove/{bookId}")
	public ResponseEntity<?> removeItemFromCart(@PathVariable Long bookId) {
		Long userId = authService.getCurrentUser().getId();
		cartService.removeItemFromCart(userId, bookId);
		return ResponseEntity.ok(new CartItemDto.MessageResponse("Item removed from cart"));
	}

	@DeleteMapping("/clear")
	public ResponseEntity<?> clearCart() {
		Long userId = authService.getCurrentUser().getId();
		cartService.clearCart(userId);
		return ResponseEntity.ok(new CartItemDto.MessageResponse("Cart cleared successfully"));
	}

	@GetMapping("/count")
	public ResponseEntity<Integer> getCartItemCount() {
		Long userId = authService.getCurrentUser().getId();
		Integer count = cartService.getCartItemCount(userId);
		return ResponseEntity.ok(count);
	}
}
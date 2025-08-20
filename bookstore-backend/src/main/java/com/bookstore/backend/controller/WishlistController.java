// controller/WishlistController.java
package com.bookstore.backend.controller;

import com.bookstore.backend.dto.WishlistDto;
import com.bookstore.backend.entity.Wishlist;
import com.bookstore.backend.entity.WishlistItem;
import com.bookstore.backend.service.AuthService;
import com.bookstore.backend.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/wishlist")
@CrossOrigin(origins = "*", maxAge = 3600)
public class WishlistController {

	@Autowired
	private WishlistService wishlistService;

	@Autowired
	private AuthService authService;

	@GetMapping
	public ResponseEntity<Wishlist> getWishlist() {
		Long userId = authService.getCurrentUser().getId();
		Wishlist wishlist = wishlistService.getWishlistByUserId(userId);
		return ResponseEntity.ok(wishlist);
	}

	@PostMapping("/add")
	public ResponseEntity<WishlistItem> addItemToWishlist(
			@Valid @RequestBody WishlistDto.AddToWishlistRequest request) {
		Long userId = authService.getCurrentUser().getId();
		WishlistItem wishlistItem = wishlistService.addItemToWishlist(userId, request.getBookId());
		return ResponseEntity.ok(wishlistItem);
	}

	@DeleteMapping("/remove/{bookId}")
	public ResponseEntity<?> removeItemFromWishlist(@PathVariable Long bookId) {
		Long userId = authService.getCurrentUser().getId();
		wishlistService.removeItemFromWishlist(userId, bookId);
		return ResponseEntity.ok(new WishlistDto.MessageResponse("Item removed from wishlist"));
	}

	@DeleteMapping("/clear")
	public ResponseEntity<?> clearWishlist() {
		Long userId = authService.getCurrentUser().getId();
		wishlistService.clearWishlist(userId);
		return ResponseEntity.ok(new WishlistDto.MessageResponse("Wishlist cleared successfully"));
	}

	@GetMapping("/count")
	public ResponseEntity<Integer> getWishlistItemCount() {
		Long userId = authService.getCurrentUser().getId();
		Integer count = wishlistService.getWishlistItemCount(userId);
		return ResponseEntity.ok(count);
	}

	@GetMapping("/check/{bookId}")
	public ResponseEntity<Boolean> isBookInWishlist(@PathVariable Long bookId) {
		Long userId = authService.getCurrentUser().getId();
		Boolean isInWishlist = wishlistService.isBookInWishlist(userId, bookId);
		return ResponseEntity.ok(isInWishlist);
	}
}
// service/WishlistService.java
package com.bookstore.backend.service;

import com.bookstore.backend.entity.Wishlist;
import com.bookstore.backend.entity.WishlistItem;
import com.bookstore.backend.entity.Book;
import com.bookstore.backend.entity.User;
import com.bookstore.backend.repository.WishlistRepository;
import com.bookstore.backend.repository.WishlistItemRepository;
import com.bookstore.backend.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class WishlistService {

	@Autowired
	private WishlistRepository wishlistRepository;

	@Autowired
	private WishlistItemRepository wishlistItemRepository;

	@Autowired
	private BookService bookService;

	@Autowired
	private UserService userService;

	public Wishlist getWishlistByUserId(Long userId) {
		return wishlistRepository.findByUserIdWithItems(userId).orElseGet(() -> {
			User user = userService.getUserById(userId);
			Wishlist wishlist = new Wishlist(user);
			return wishlistRepository.save(wishlist);
		});
	}

	public WishlistItem addItemToWishlist(Long userId, Long bookId) {
		Wishlist wishlist = getWishlistByUserId(userId);
		Book book = bookService.getBookById(bookId);

		// Check if book is already in wishlist
		if (wishlistItemRepository.existsByWishlistAndBook(wishlist, book)) {
			throw new RuntimeException("Book is already in wishlist");
		}

		WishlistItem wishlistItem = new WishlistItem(wishlist, book);
		return wishlistItemRepository.save(wishlistItem);
	}

	public void removeItemFromWishlist(Long userId, Long bookId) {
		Wishlist wishlist = getWishlistByUserId(userId);
		Book book = bookService.getBookById(bookId);

		WishlistItem wishlistItem = wishlistItemRepository.findByWishlistAndBook(wishlist, book)
				.orElseThrow(() -> new ResourceNotFoundException("Item not found in wishlist"));

		wishlistItemRepository.delete(wishlistItem);
	}

	public void clearWishlist(Long userId) {
		Wishlist wishlist = getWishlistByUserId(userId);
		wishlistItemRepository.deleteAllByWishlistId(wishlist.getId());
	}

	public Integer getWishlistItemCount(Long userId) {
		return wishlistItemRepository.countItemsByUserId(userId);
	}

	public boolean isBookInWishlist(Long userId, Long bookId) {
		Wishlist wishlist = getWishlistByUserId(userId);
		Book book = bookService.getBookById(bookId);
		return wishlistItemRepository.existsByWishlistAndBook(wishlist, book);
	}
}
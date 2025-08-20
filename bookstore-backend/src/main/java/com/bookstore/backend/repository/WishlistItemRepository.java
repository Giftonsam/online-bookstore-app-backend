// repository/WishlistItemRepository.java
package com.bookstore.backend.repository;

import com.bookstore.backend.entity.WishlistItem;
import com.bookstore.backend.entity.Wishlist;
import com.bookstore.backend.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {

	List<WishlistItem> findByWishlist(Wishlist wishlist);

	Optional<WishlistItem> findByWishlistAndBook(Wishlist wishlist, Book book);

	void deleteByWishlist(Wishlist wishlist);

	void deleteByWishlistAndBook(Wishlist wishlist, Book book);

	@Modifying
	@Query("DELETE FROM WishlistItem wi WHERE wi.wishlist.id = :wishlistId")
	void deleteAllByWishlistId(@Param("wishlistId") Long wishlistId);

	@Query("SELECT COUNT(wi) FROM WishlistItem wi WHERE wi.wishlist.user.id = :userId")
	Integer countItemsByUserId(@Param("userId") Long userId);

	Boolean existsByWishlistAndBook(Wishlist wishlist, Book book);
}
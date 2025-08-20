// repository/WishlistRepository.java
package com.bookstore.backend.repository;

import com.bookstore.backend.entity.Wishlist;
import com.bookstore.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

	Optional<Wishlist> findByUser(User user);

	Optional<Wishlist> findByUserId(Long userId);

	@Query("SELECT w FROM Wishlist w LEFT JOIN FETCH w.wishlistItems wi LEFT JOIN FETCH wi.book WHERE w.user.id = :userId")
	Optional<Wishlist> findByUserIdWithItems(@Param("userId") Long userId);

	void deleteByUser(User user);
}
// repository/CartItemRepository.java
package com.bookstore.backend.repository;

import com.bookstore.backend.entity.Cart;
import com.bookstore.backend.entity.CartItem;
import com.bookstore.backend.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

	List<CartItem> findByCart(Cart cart);

	Optional<CartItem> findByCartAndBook(Cart cart, Book book);

	void deleteByCart(Cart cart);

	void deleteByCartAndBook(Cart cart, Book book);

	@Modifying
	@Query("DELETE FROM CartItem ci WHERE ci.cart.id = :cartId")
	void deleteAllByCartId(@Param("cartId") Long cartId);

	@Query("SELECT COUNT(ci) FROM CartItem ci WHERE ci.cart.user.id = :userId")
	Integer countItemsByUserId(@Param("userId") Long userId);
}
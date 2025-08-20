// repository/CartRepository.java
package com.bookstore.backend.repository;

import com.bookstore.backend.entity.Cart;
import com.bookstore.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

	Optional<Cart> findByUser(User user);

	Optional<Cart> findByUserId(Long userId);

	@Query("SELECT c FROM Cart c LEFT JOIN FETCH c.cartItems ci LEFT JOIN FETCH ci.book WHERE c.user.id = :userId")
	Optional<Cart> findByUserIdWithItems(@Param("userId") Long userId);

	void deleteByUser(User user);
}
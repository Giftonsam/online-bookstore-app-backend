// repository/OrderRepository.java
package com.bookstore.backend.repository;

import com.bookstore.backend.entity.Order;
import com.bookstore.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

	Page<Order> findByUser(User user, Pageable pageable);

	Page<Order> findByUserOrderByOrderDateDesc(User user, Pageable pageable);

	Optional<Order> findByOrderNumber(String orderNumber);

	List<Order> findByStatus(Order.OrderStatus status);

	Page<Order> findByStatus(Order.OrderStatus status, Pageable pageable);

	@Query("SELECT o FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate")
	List<Order> findOrdersBetweenDates(@Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate);

	@Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status = :status AND "
			+ "o.orderDate BETWEEN :startDate AND :endDate")
	BigDecimal getTotalRevenueByStatusAndDateRange(@Param("status") Order.OrderStatus status,
			@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

	@Query("SELECT COUNT(o) FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate")
	Long countOrdersBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

	@Query("SELECT o FROM Order o ORDER BY o.orderDate DESC")
	Page<Order> findAllOrdersOrderByDateDesc(Pageable pageable);

	List<Order> findTop10ByOrderByOrderDateDesc();

	// Add these methods to your existing OrderRepository interface

	Page<Order> findByOrderDateAfter(LocalDateTime startDate, Pageable pageable);

	Page<Order> findByStatusAndOrderDateAfter(Order.OrderStatus status, LocalDateTime startDate, Pageable pageable);

	Page<Order> findByUserFirstNameContainingIgnoreCaseOrUserLastNameContainingIgnoreCase(String firstName,
			String lastName, Pageable pageable);

	Page<Order> findByUserFirstNameContainingIgnoreCaseOrUserLastNameContainingIgnoreCaseAndOrderDateAfter(
			String firstName, String lastName, LocalDateTime startDate, Pageable pageable);

	Page<Order> findByStatusAndUserFirstNameContainingIgnoreCaseOrUserLastNameContainingIgnoreCase(
			Order.OrderStatus status, String firstName, String lastName, Pageable pageable);

	Page<Order> findByStatusAndUserFirstNameContainingIgnoreCaseOrUserLastNameContainingIgnoreCaseAndOrderDateAfter(
			Order.OrderStatus status, String firstName, String lastName, LocalDateTime startDate, Pageable pageable);

	List<Order> findByUserIdOrderByOrderDateDesc(Long userId);
}
// repository/OrderItemRepository.java
package com.bookstore.backend.repository;

import com.bookstore.backend.entity.OrderItem;
import com.bookstore.backend.entity.Order;
import com.bookstore.backend.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

	List<OrderItem> findByOrder(Order order);

	List<OrderItem> findByBook(Book book);

	@Query("SELECT oi FROM OrderItem oi WHERE oi.order.orderDate BETWEEN :startDate AND :endDate")
	List<OrderItem> findOrderItemsBetweenDates(@Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate);

	@Query("SELECT oi.book, SUM(oi.quantity) as totalSold FROM OrderItem oi " + "WHERE oi.order.status = 'DELIVERED' "
			+ "GROUP BY oi.book ORDER BY totalSold DESC")
	List<Object[]> findBestSellingBooks();

	@Query("SELECT SUM(oi.quantity) FROM OrderItem oi WHERE oi.book.id = :bookId")
	Integer getTotalQuantitySoldForBook(@Param("bookId") Long bookId);

	boolean existsByBookId(Long bookId);
}
// repository/PaymentRepository.java
package com.bookstore.backend.repository;

import com.bookstore.backend.entity.Payment;
import com.bookstore.backend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

	Optional<Payment> findByOrder(Order order);

	Optional<Payment> findByPaymentId(String paymentId);

	Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);

	List<Payment> findByStatus(Payment.PaymentStatus status);

	@Query("SELECT p FROM Payment p WHERE p.paymentDate BETWEEN :startDate AND :endDate")
	List<Payment> findPaymentsBetweenDates(@Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate);

	@Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = :status AND "
			+ "p.paymentDate BETWEEN :startDate AND :endDate")
	BigDecimal getTotalAmountByStatusAndDateRange(@Param("status") Payment.PaymentStatus status,
			@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

	@Query("SELECT COUNT(p) FROM Payment p WHERE p.status = 'SUCCESS'")
	Long countSuccessfulPayments();

	@Query("SELECT COUNT(p) FROM Payment p WHERE p.status = 'FAILED'")
	Long countFailedPayments();
}
// entity/Payment.java
package com.bookstore.backend.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id")
	private Order order;

	@NotBlank
	@Column(name = "payment_id")
	private String paymentId; // Razorpay payment ID

	@NotBlank
	@Column(name = "razorpay_order_id")
	private String razorpayOrderId;

	@Column(name = "razorpay_signature")
	private String razorpaySignature;

	@NotNull
	@Column(precision = 10, scale = 2)
	private BigDecimal amount;

	@Enumerated(EnumType.STRING)
	@Column(length = 20)
	private PaymentStatus status = PaymentStatus.PENDING;

	@Enumerated(EnumType.STRING)
	@Column(length = 20)
	private PaymentMethod method;

	@Column(name = "payment_date")
	private LocalDateTime paymentDate = LocalDateTime.now();

	@Column(name = "updated_at")
	private LocalDateTime updatedAt = LocalDateTime.now();

	// Constructors
	public Payment() {
	}

	public Payment(Order order, String razorpayOrderId, BigDecimal amount) {
		this.order = order;
		this.razorpayOrderId = razorpayOrderId;
		this.amount = amount;
	}

	// Getters and Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	public String getRazorpayOrderId() {
		return razorpayOrderId;
	}

	public void setRazorpayOrderId(String razorpayOrderId) {
		this.razorpayOrderId = razorpayOrderId;
	}

	public String getRazorpaySignature() {
		return razorpaySignature;
	}

	public void setRazorpaySignature(String razorpaySignature) {
		this.razorpaySignature = razorpaySignature;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public PaymentStatus getStatus() {
		return status;
	}

	public void setStatus(PaymentStatus status) {
		this.status = status;
	}

	public PaymentMethod getMethod() {
		return method;
	}

	public void setMethod(PaymentMethod method) {
		this.method = method;
	}

	public LocalDateTime getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(LocalDateTime paymentDate) {
		this.paymentDate = paymentDate;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	@PreUpdate
	public void preUpdate() {
		this.updatedAt = LocalDateTime.now();
	}

	public enum PaymentStatus {
		PENDING, SUCCESS, FAILED, CANCELLED, REFUNDED
	}

	public enum PaymentMethod {
		RAZORPAY, CREDIT_CARD, DEBIT_CARD, UPI, NET_BANKING, WALLET
	}
}
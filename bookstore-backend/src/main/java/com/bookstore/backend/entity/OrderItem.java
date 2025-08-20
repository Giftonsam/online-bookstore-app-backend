// entity/OrderItem.java
package com.bookstore.backend.entity;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
public class OrderItem {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id")
	private Order order;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "book_id")
	private Book book;

	@NotNull
	@Min(1)
	private Integer quantity;

	@NotNull
	@Column(name = "unit_price", precision = 10, scale = 2)
	private BigDecimal unitPrice;

	@NotNull
	@Column(name = "total_price", precision = 10, scale = 2)
	private BigDecimal totalPrice;

	// Constructors
	public OrderItem() {
	}

	public OrderItem(Order order, Book book, Integer quantity, BigDecimal unitPrice) {
		this.order = order;
		this.book = book;
		this.quantity = quantity;
		this.unitPrice = unitPrice;
		this.totalPrice = unitPrice.multiply(new BigDecimal(quantity));
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

	public Book getBook() {
		return book;
	}

	public void setBook(Book book) {
		this.book = book;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}
}
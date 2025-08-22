// service/OrderService.java
package com.bookstore.backend.service;

import com.bookstore.backend.entity.*;
import com.bookstore.backend.repository.OrderRepository;
import com.bookstore.backend.repository.OrderItemRepository;
import com.bookstore.backend.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private OrderItemRepository orderItemRepository;

	@Autowired
	private CartService cartService;

	@Autowired
	private BookService bookService;

	@Autowired
	private UserService userService;

	@Autowired
	private EmailService emailService;

	public Order createOrder(Long userId, String shippingAddress) {
		User user = userService.getUserById(userId);
		Cart cart = cartService.getCartByUserId(userId);

		if (cart.getCartItems().isEmpty()) {
			throw new RuntimeException("Cart is empty");
		}

		// Calculate total amount
		BigDecimal totalAmount = cart.getTotalPrice();

		// Create order
		Order order = new Order(user, totalAmount, shippingAddress);
		Order savedOrder = orderRepository.save(order);

		// Create order items from cart items
		for (CartItem cartItem : cart.getCartItems()) {
			OrderItem orderItem = new OrderItem(savedOrder, cartItem.getBook(), cartItem.getQuantity(),
					cartItem.getBook().getCurrentPrice());
			orderItemRepository.save(orderItem);

			// Update book stock
			bookService.updateStock(cartItem.getBook().getId(), cartItem.getQuantity());
		}

		// Clear cart after order creation
		cartService.clearCart(userId);

		// Send order confirmation email
		emailService.sendOrderConfirmationEmail(savedOrder);

		return savedOrder;
	}

	public Order getOrderById(Long id) {
		return orderRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
	}

	public Order getOrderByOrderNumber(String orderNumber) {
		return orderRepository.findByOrderNumber(orderNumber)
				.orElseThrow(() -> new ResourceNotFoundException("Order not found with order number: " + orderNumber));
	}

	public Page<Order> getOrdersByUser(Long userId, int page, int size) {
		User user = userService.getUserById(userId);
		Pageable pageable = PageRequest.of(page, size);
		return orderRepository.findByUserOrderByOrderDateDesc(user, pageable);
	}

	public Page<Order> getAllOrders(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return orderRepository.findAllOrdersOrderByDateDesc(pageable);
	}

	public Order updateOrderStatus(Long orderId, Order.OrderStatus status) {
		Order order = getOrderById(orderId);
		order.setStatus(status);

		if (status == Order.OrderStatus.DELIVERED) {
			order.setDeliveryDate(LocalDateTime.now());
		}

		Order updatedOrder = orderRepository.save(order);

		// Send email notification for status changes
		if (status == Order.OrderStatus.SHIPPED) {
			emailService.sendOrderShippedEmail(updatedOrder);
		}

		return updatedOrder;
	}

	public List<Order> getOrdersByStatus(Order.OrderStatus status) {
		return orderRepository.findByStatus(status);
	}

	public List<Order> getRecentOrders() {
		return orderRepository.findTop10ByOrderByOrderDateDesc();
	}

	public BigDecimal getTotalRevenue(LocalDateTime startDate, LocalDateTime endDate) {
		return orderRepository.getTotalRevenueByStatusAndDateRange(Order.OrderStatus.DELIVERED, startDate, endDate);
	}

	public Long getOrderCount(LocalDateTime startDate, LocalDateTime endDate) {
		return orderRepository.countOrdersBetweenDates(startDate, endDate);
	}

	public Optional<Order> findById(Long id) {
		return orderRepository.findById(id);
	}
}
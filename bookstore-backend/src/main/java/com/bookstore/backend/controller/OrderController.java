// controller/OrderController.java
package com.bookstore.backend.controller;

import com.bookstore.backend.dto.OrderDto;
import com.bookstore.backend.entity.Order;
import com.bookstore.backend.entity.User;
import com.bookstore.backend.service.AuthService;
import com.bookstore.backend.service.OrderService;
import com.bookstore.backend.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*", maxAge = 3600)
public class OrderController {

	@Autowired
	private OrderService orderService;

	@Autowired
	private AuthService authService;

	@PostMapping
	public ResponseEntity<Order> createOrder(@Valid @RequestBody OrderDto.CreateOrderRequest request) {
		Long userId = authService.getCurrentUser().getId();
		Order order = orderService.createOrder(userId, request.getShippingAddress());
		return ResponseEntity.ok(order);
	}

	@GetMapping
	public ResponseEntity<Page<Order>> getUserOrders(
			@RequestParam(defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
			@RequestParam(defaultValue = Constants.DEFAULT_PAGE_SIZE) int size) {

		Long userId = authService.getCurrentUser().getId();
		Page<Order> orders = orderService.getOrdersByUser(userId, page, size);
		return ResponseEntity.ok(orders);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
		Order order = orderService.getOrderById(id);

		// Check if user owns this order or is admin
		Long currentUserId = authService.getCurrentUser().getId();
		if (!order.getUser().getId().equals(currentUserId)
				&& !authService.getCurrentUser().getRole().equals(User.Role.ADMIN)) {
			return ResponseEntity.status(403).build();
		}

		return ResponseEntity.ok(order);
	}

	@GetMapping("/number/{orderNumber}")
	public ResponseEntity<Order> getOrderByOrderNumber(@PathVariable String orderNumber) {
		Order order = orderService.getOrderByOrderNumber(orderNumber);

		// Check if user owns this order or is admin
		Long currentUserId = authService.getCurrentUser().getId();
		if (!order.getUser().getId().equals(currentUserId)
				&& !authService.getCurrentUser().getRole().equals(User.Role.ADMIN)) {
			return ResponseEntity.status(403).build();
		}

		return ResponseEntity.ok(order);
	}

	@GetMapping("/admin/all")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Page<Order>> getAllOrders(
			@RequestParam(defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
			@RequestParam(defaultValue = Constants.DEFAULT_PAGE_SIZE) int size) {

		Page<Order> orders = orderService.getAllOrders(page, size);
		return ResponseEntity.ok(orders);
	}

	@PutMapping("/{id}/status")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Order> updateOrderStatus(@PathVariable Long id,
			@Valid @RequestBody OrderDto.UpdateOrderStatusRequest request) {

		Order updatedOrder = orderService.updateOrderStatus(id, request.getStatus());
		return ResponseEntity.ok(updatedOrder);
	}

	@GetMapping("/admin/status/{status}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<Order>> getOrdersByStatus(@PathVariable Order.OrderStatus status) {
		List<Order> orders = orderService.getOrdersByStatus(status);
		return ResponseEntity.ok(orders);
	}

	@GetMapping("/admin/recent")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<Order>> getRecentOrders() {
		List<Order> orders = orderService.getRecentOrders();
		return ResponseEntity.ok(orders);
	}
}
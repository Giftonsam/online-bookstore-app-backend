// controller/AdminController.java
package com.bookstore.backend.controller;

import com.bookstore.backend.dto.AdminDto;
import com.bookstore.backend.entity.Book;
import com.bookstore.backend.entity.Order;
import com.bookstore.backend.entity.User;
import com.bookstore.backend.service.AdminService;
import com.bookstore.backend.service.BookService;
import com.bookstore.backend.service.OrderService;
import com.bookstore.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"}, maxAge = 3600)
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

	@Autowired
	private AdminService adminService;

	@Autowired
	private BookService bookService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private UserService userService;

	// ============ EXISTING ENDPOINTS ============
	@GetMapping("/dashboard")
	public ResponseEntity<Map<String, Object>> getDashboardStats() {
		Map<String, Object> stats = adminService.getDashboardStats();
		return ResponseEntity.ok(stats);
	}

	@GetMapping("/sales-report")
	public ResponseEntity<Map<String, Object>> getSalesReport(@RequestParam(required = false) LocalDateTime startDate,
			@RequestParam(required = false) LocalDateTime endDate) {
		Map<String, Object> report = adminService.getSalesReport(startDate, endDate);
		return ResponseEntity.ok(report);
	}

	@GetMapping("/low-stock-books")
	public ResponseEntity<?> getLowStockBooks() {
		return ResponseEntity.ok(adminService.getLowStockBooks());
	}

	@GetMapping("/best-selling-books")
	public ResponseEntity<?> getBestSellingBooks() {
		return ResponseEntity.ok(adminService.getBestSellingBooks());
	}

	@PostMapping("/export-orders")
	public ResponseEntity<?> exportOrders(@RequestBody AdminDto.ExportRequest request) {
		String filePath = adminService.exportOrdersToCSV(request.getStartDate(), request.getEndDate());
		return ResponseEntity.ok(new AdminDto.ExportResponse("Orders exported successfully!", filePath));
	}

	@PostMapping("/export-books")
	public ResponseEntity<?> exportBooks() {
		String filePath = adminService.exportBooksToCSV();
		return ResponseEntity.ok(new AdminDto.ExportResponse("Books exported successfully!", filePath));
	}

	@PostMapping("/import-books")
	public ResponseEntity<?> importBooks(@RequestParam("file") MultipartFile file) {
		int importedCount = adminService.importBooksFromCSV(file);
		return ResponseEntity.ok(new AdminDto.ImportResponse("Books imported successfully!", importedCount));
	}

	// ============ NEW ENDPOINTS FOR FRONTEND INTEGRATION ============

	// Dashboard Stats (Frontend compatible format)
	@GetMapping("/dashboard/stats")
	public ResponseEntity<Map<String, Object>> getDashboardStatsForFrontend(
			@RequestParam(defaultValue = "week") String range) {
		try {
			Map<String, Object> stats = adminService.getDashboardStats();
			
			// Transform to frontend-expected format
			Map<String, Object> frontendStats = new HashMap<>();
			frontendStats.put("success", true);
			frontendStats.put("data", transformStatsForFrontend(stats, range));
			
			return ResponseEntity.ok(frontendStats);
		} catch (Exception e) {
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("success", false);
			errorResponse.put("message", "Failed to fetch dashboard statistics");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}

	// Recent Orders for Dashboard
	@GetMapping("/orders/recent")
	public ResponseEntity<Map<String, Object>> getRecentOrders(
			@RequestParam(defaultValue = "5") int limit) {
		try {
			List<Order> recentOrders = adminService.getRecentOrdersWithLimit(limit);
			
			Map<String, Object> response = new HashMap<>();
			response.put("success", true);
			response.put("data", recentOrders);
			
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("success", false);
			errorResponse.put("message", "Failed to fetch recent orders");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}

	// Top Selling Books for Dashboard
	@GetMapping("/books/top-selling")
	public ResponseEntity<Map<String, Object>> getTopSellingBooks(
			@RequestParam(defaultValue = "5") int limit) {
		try {
			List<Book> topBooks = adminService.getTopSellingBooksWithLimit(limit);
			
			Map<String, Object> response = new HashMap<>();
			response.put("success", true);
			response.put("data", topBooks);
			
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("success", false);
			errorResponse.put("message", "Failed to fetch top selling books");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}

	// Books Management with Pagination and Search
	@GetMapping("/books")
	public ResponseEntity<Map<String, Object>> getAllBooks(
			@RequestParam(required = false) String search,
			@RequestParam(required = false) String category,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "50") int size) {
		try {
			Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
			Page<Book> books = adminService.getAllBooksWithFilters(search, category, pageable);
			
			Map<String, Object> response = new HashMap<>();
			response.put("success", true);
			response.put("data", books.getContent());
			response.put("totalPages", books.getTotalPages());
			response.put("totalElements", books.getTotalElements());
			
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("success", false);
			errorResponse.put("message", "Failed to fetch books");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}

	// Get Single Book
	@GetMapping("/books/{id}")
	public ResponseEntity<Map<String, Object>> getBookById(@PathVariable Long id) {
		try {
			Optional<Book> book = bookService.findById(id);
			
			Map<String, Object> response = new HashMap<>();
			if (book.isPresent()) {
				response.put("success", true);
				response.put("data", book.get());
				return ResponseEntity.ok(response);
			} else {
				response.put("success", false);
				response.put("message", "Book not found");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
			}
		} catch (Exception e) {
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("success", false);
			errorResponse.put("message", "Failed to fetch book");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}

	// Create Book
	@PostMapping("/books")
	public ResponseEntity<Map<String, Object>> createBook(@RequestBody Book book) {
		try {
			// Validate required fields
			if (book.getTitle() == null || book.getAuthor() == null || 
				book.getPrice() == null || book.getStockQuantity() == null) {
				Map<String, Object> errorResponse = new HashMap<>();
				errorResponse.put("success", false);
				errorResponse.put("message", "Missing required fields: title, author, price, stockQuantity");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
			}

			Book savedBook = bookService.save(book);
			
			Map<String, Object> response = new HashMap<>();
			response.put("success", true);
			response.put("data", savedBook);
			response.put("message", "Book created successfully");
			
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		} catch (Exception e) {
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("success", false);
			errorResponse.put("message", "Failed to create book");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}

	// Update Book
	@PutMapping("/books/{id}")
	public ResponseEntity<Map<String, Object>> updateBook(@PathVariable Long id, @RequestBody Book book) {
		try {
			Optional<Book> existingBook = bookService.findById(id);
			
			Map<String, Object> response = new HashMap<>();
			if (!existingBook.isPresent()) {
				response.put("success", false);
				response.put("message", "Book not found");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
			}

			book.setId(id);
			Book updatedBook = bookService.save(book);
			
			response.put("success", true);
			response.put("data", updatedBook);
			response.put("message", "Book updated successfully");
			
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("success", false);
			errorResponse.put("message", "Failed to update book");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}

	// Delete Book
	@DeleteMapping("/books/{id}")
	public ResponseEntity<Map<String, Object>> deleteBook(@PathVariable Long id) {
		try {
			Optional<Book> existingBook = bookService.findById(id);
			
			Map<String, Object> response = new HashMap<>();
			if (!existingBook.isPresent()) {
				response.put("success", false);
				response.put("message", "Book not found");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
			}

			// Check if book is in any orders
			boolean hasOrders = adminService.hasBookInOrders(id);
			if (hasOrders) {
				response.put("success", false);
				response.put("message", "Cannot delete book that has been ordered. Consider marking it as unavailable instead.");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
			}

			bookService.deleteById(id);
			
			response.put("success", true);
			response.put("message", "Book deleted successfully");
			
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("success", false);
			errorResponse.put("message", "Failed to delete book");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}

	// Stock Management
	@PutMapping("/books/{id}/stock")
	public ResponseEntity<Map<String, Object>> updateBookStock(
			@PathVariable Long id, @RequestBody Map<String, Object> stockData) {
		try {
			String operation = (String) stockData.get("operation");
			Integer quantity = (Integer) stockData.get("quantity");
			String reason = (String) stockData.get("reason");

			Map<String, Object> response = new HashMap<>();
			if (operation == null || quantity == null) {
				response.put("success", false);
				response.put("message", "Operation and quantity are required");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
			}

			Map<String, Object> result = adminService.updateBookStock(id, operation, quantity, reason);
			
			response.put("success", true);
			response.put("data", result);
			response.put("message", "Stock updated successfully");
			
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("success", false);
			errorResponse.put("message", "Failed to update stock");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}

	// Orders Management
	@GetMapping("/orders")
	public ResponseEntity<Map<String, Object>> getAllOrders(
			@RequestParam(required = false) String status,
			@RequestParam(required = false) String search,
			@RequestParam(required = false) String dateRange,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "50") int size) {
		try {
			Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());
			Page<Order> orders = adminService.getAllOrdersWithFilters(status, search, dateRange, pageable);
			
			Map<String, Object> response = new HashMap<>();
			response.put("success", true);
			response.put("data", orders.getContent());
			response.put("totalPages", orders.getTotalPages());
			response.put("totalElements", orders.getTotalElements());
			
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("success", false);
			errorResponse.put("message", "Failed to fetch orders");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}

	// Get Single Order
	@GetMapping("/orders/{id}")
	public ResponseEntity<Map<String, Object>> getOrderById(@PathVariable Long id) {
		try {
			Optional<Order> order = orderService.findById(id);
			
			Map<String, Object> response = new HashMap<>();
			if (order.isPresent()) {
				response.put("success", true);
				response.put("data", order.get());
				return ResponseEntity.ok(response);
			} else {
				response.put("success", false);
				response.put("message", "Order not found");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
			}
		} catch (Exception e) {
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("success", false);
			errorResponse.put("message", "Failed to fetch order");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}

	// Update Order Status
	@PutMapping("/orders/{id}/status")
	public ResponseEntity<Map<String, Object>> updateOrderStatus(
			@PathVariable Long id, @RequestBody Map<String, String> statusData) {
		try {
			String status = statusData.get("status");
			String note = statusData.get("note");

			Map<String, Object> response = new HashMap<>();
			if (status == null) {
				response.put("success", false);
				response.put("message", "Status is required");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
			}

			boolean updated = adminService.updateOrderStatus(id, status, note);
			if (updated) {
				response.put("success", true);
				response.put("message", "Order status updated successfully");
				return ResponseEntity.ok(response);
			} else {
				response.put("success", false);
				response.put("message", "Order not found");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
			}
		} catch (Exception e) {
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("success", false);
			errorResponse.put("message", "Failed to update order status");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}

	// Users Management
	@GetMapping("/users")
	public ResponseEntity<Map<String, Object>> getAllUsers(
			@RequestParam(required = false) String search,
			@RequestParam(required = false) String status,
			@RequestParam(required = false) String usertype,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "50") int size) {
		try {
			Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
			Page<User> users = adminService.getAllUsersWithFilters(search, status, usertype, pageable);
			
			Map<String, Object> response = new HashMap<>();
			response.put("success", true);
			response.put("data", users.getContent());
			response.put("totalPages", users.getTotalPages());
			response.put("totalElements", users.getTotalElements());
			
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("success", false);
			errorResponse.put("message", "Failed to fetch users");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}

	// Get Single User
	@GetMapping("/users/{id}")
	public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long id) {
		try {
			Optional<User> user = userService.findById(id);
			
			Map<String, Object> response = new HashMap<>();
			if (user.isPresent()) {
				response.put("success", true);
				response.put("data", user.get());
				return ResponseEntity.ok(response);
			} else {
				response.put("success", false);
				response.put("message", "User not found");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
			}
		} catch (Exception e) {
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("success", false);
			errorResponse.put("message", "Failed to fetch user");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}

	// Update User Status
	@PutMapping("/users/{id}/status")
	public ResponseEntity<Map<String, Object>> updateUserStatus(
			@PathVariable Long id, @RequestBody Map<String, String> statusData) {
		try {
			String status = statusData.get("status");

			Map<String, Object> response = new HashMap<>();
			if (status == null) {
				response.put("success", false);
				response.put("message", "Status is required");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
			}

			boolean updated = adminService.updateUserStatus(id, status);
			if (updated) {
				response.put("success", true);
				response.put("message", "User status updated successfully");
				return ResponseEntity.ok(response);
			} else {
				response.put("success", false);
				response.put("message", "User not found or cannot update admin user");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
			}
		} catch (Exception e) {
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("success", false);
			errorResponse.put("message", "Failed to update user status");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}

	// Get User Orders
	@GetMapping("/users/{id}/orders")
	public ResponseEntity<Map<String, Object>> getUserOrders(
			@PathVariable Long id,
			@RequestParam(defaultValue = "10") int limit) {
		try {
			List<Order> userOrders = adminService.getUserOrders(id, limit);
			
			Map<String, Object> response = new HashMap<>();
			response.put("success", true);
			response.put("data", userOrders);
			
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("success", false);
			errorResponse.put("message", "Failed to fetch user orders");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}

	// Categories
	@GetMapping("/categories")
	public ResponseEntity<Map<String, Object>> getAllCategories() {
		try {
			List<Map<String, Object>> categories = adminService.getAllCategories();
			
			Map<String, Object> response = new HashMap<>();
			response.put("success", true);
			response.put("data", categories);
			
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("success", false);
			errorResponse.put("message", "Failed to fetch categories");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}

	@PostMapping("/categories")
	public ResponseEntity<Map<String, Object>> createCategory(@RequestBody Map<String, String> categoryData) {
		try {
			String name = categoryData.get("name");
			
			Map<String, Object> response = new HashMap<>();
			if (name == null || name.trim().isEmpty()) {
				response.put("success", false);
				response.put("message", "Category name is required");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
			}

			Map<String, Object> newCategory = adminService.createCategory(name);
			
			response.put("success", true);
			response.put("data", newCategory);
			response.put("message", "Category created successfully");
			
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		} catch (Exception e) {
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("success", false);
			errorResponse.put("message", "Failed to create category");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}

	// Helper method to transform stats for frontend
	private Map<String, Object> transformStatsForFrontend(Map<String, Object> stats, String range) {
		Map<String, Object> transformed = new HashMap<>();
		
		// Basic stats
		transformed.put("totalBooks", stats.get("totalBooks"));
		transformed.put("totalOrders", stats.get("totalOrders"));
		transformed.put("totalRevenue", stats.get("monthlyRevenue"));
		transformed.put("lowStockBooks", stats.get("lowStockBooks"));
		transformed.put("outOfStockBooks", 0); // You can calculate this
		
		// Trends (you can calculate based on previous period)
		transformed.put("salesTrend", "up");
		transformed.put("ordersTrend", "up");
		transformed.put("revenueTrend", "up");
		
		return transformed;
	}
}
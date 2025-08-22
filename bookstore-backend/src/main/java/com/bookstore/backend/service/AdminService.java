// service/AdminService.java
package com.bookstore.backend.service;

import com.bookstore.backend.entity.Category;
import com.bookstore.backend.entity.Book;
import com.bookstore.backend.entity.Order;
import com.bookstore.backend.entity.User;
import com.bookstore.backend.repository.BookRepository;
import com.bookstore.backend.repository.OrderRepository;
import com.bookstore.backend.repository.OrderItemRepository;
import com.bookstore.backend.repository.UserRepository;
import com.bookstore.backend.repository.PaymentRepository;
import com.opencsv.CSVWriter;
import com.opencsv.CSVReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileWriter;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList; // Add this import
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminService {

	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private OrderItemRepository orderItemRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PaymentRepository paymentRepository;

	@Autowired
	private BookService bookService;

	@Autowired
	private CategoryService categoryService;

	public Map<String, Object> getDashboardStats() {
		Map<String, Object> stats = new HashMap<>();

		// Basic counts
		stats.put("totalBooks", bookRepository.countActiveBooks());
		stats.put("totalUsers", userRepository.countActiveUsersByRole(User.Role.USER));
		stats.put("totalOrders", orderRepository.count());
		stats.put("lowStockBooks", bookRepository.countLowStockBooks(10));

		// Revenue stats
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime startOfMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
		BigDecimal monthlyRevenue = orderRepository.getTotalRevenueByStatusAndDateRange(Order.OrderStatus.DELIVERED,
				startOfMonth, now);
		stats.put("monthlyRevenue", monthlyRevenue != null ? monthlyRevenue : BigDecimal.ZERO);

		// Recent orders
		stats.put("recentOrders", orderRepository.findTop10ByOrderByOrderDateDesc());

		// Payment stats
		stats.put("successfulPayments", paymentRepository.countSuccessfulPayments());
		stats.put("failedPayments", paymentRepository.countFailedPayments());

		return stats;
	}

	public Map<String, Object> getSalesReport(LocalDateTime startDate, LocalDateTime endDate) {
		if (startDate == null) {
			startDate = LocalDateTime.now().minusMonths(1);
		}
		if (endDate == null) {
			endDate = LocalDateTime.now();
		}

		Map<String, Object> report = new HashMap<>();

		List<Order> orders = orderRepository.findOrdersBetweenDates(startDate, endDate);
		BigDecimal totalRevenue = orderRepository.getTotalRevenueByStatusAndDateRange(Order.OrderStatus.DELIVERED,
				startDate, endDate);
		Long orderCount = orderRepository.countOrdersBetweenDates(startDate, endDate);

		report.put("orders", orders);
		report.put("totalRevenue", totalRevenue != null ? totalRevenue : BigDecimal.ZERO);
		report.put("orderCount", orderCount);
		report.put("startDate", startDate);
		report.put("endDate", endDate);

		return report;
	}

	public List<Book> getLowStockBooks() {
		return bookRepository.findByStockQuantityLessThan(10);
	}

	public List<Object[]> getBestSellingBooks() {
		return orderItemRepository.findBestSellingBooks();
	}

	public String exportOrdersToCSV(LocalDateTime startDate, LocalDateTime endDate) {
		try {
			String fileName = "orders_export_"
					+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv";
			String filePath = "exports/" + fileName;

			List<Order> orders = orderRepository.findOrdersBetweenDates(startDate, endDate);

			CSVWriter writer = new CSVWriter(new FileWriter(filePath));

			// Write header
			String[] header = { "Order Number", "Customer", "Total Amount", "Status", "Order Date", "Delivery Date" };
			writer.writeNext(header);

			// Write data
			for (Order order : orders) {
				String[] data = { order.getOrderNumber(),
						order.getUser().getFirstName() + " " + order.getUser().getLastName(),
						order.getTotalAmount().toString(), order.getStatus().toString(),
						order.getOrderDate().toString(),
						order.getDeliveryDate() != null ? order.getDeliveryDate().toString() : "" };
				writer.writeNext(data);
			}

			writer.close();
			return filePath;
		} catch (Exception e) {
			throw new RuntimeException("Failed to export orders: " + e.getMessage());
		}
	}

	public String exportBooksToCSV() {
		try {
			String fileName = "books_export_"
					+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv";
			String filePath = "exports/" + fileName;

			List<Book> books = bookRepository.findAll();

			CSVWriter writer = new CSVWriter(new FileWriter(filePath));

			// Write header
			String[] header = { "Title", "Author", "ISBN", "Price", "Stock Quantity", "Category", "Publisher" };
			writer.writeNext(header);

			// Write data
			for (Book book : books) {
				String[] data = { book.getTitle(), book.getAuthor(), book.getIsbn(), book.getPrice().toString(),
						book.getStockQuantity().toString(),
						book.getCategory() != null ? book.getCategory().getName() : "", book.getPublisher() };
				writer.writeNext(data);
			}

			writer.close();
			return filePath;
		} catch (Exception e) {
			throw new RuntimeException("Failed to export books: " + e.getMessage());
		}
	}

	public int importBooksFromCSV(MultipartFile file) {
		try {
			CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()));
			List<String[]> records = reader.readAll();
			reader.close();

			int importedCount = 0;

			// Skip header row
			for (int i = 1; i < records.size(); i++) {
				String[] record = records.get(i);

				if (record.length >= 7) {
					Book book = new Book();
					book.setTitle(record[0]);
					book.setAuthor(record[1]);
					book.setIsbn(record[2]);
					book.setPrice(new BigDecimal(record[3]));
					book.setStockQuantity(Integer.parseInt(record[4]));

					// Set category if exists
					if (!record[5].isEmpty()) {
						try {
							Category category = categoryService.getCategoryByName(record[5]);
							book.setCategory(category);
						} catch (Exception e) {
							// Category not found, skip or create new one
						}
					}

					book.setPublisher(record[6]);
					book.setPages(100); // Default value

					bookRepository.save(book);
					importedCount++;
				}
			}

			return importedCount;
		} catch (Exception e) {
			throw new RuntimeException("Failed to import books: " + e.getMessage());
		}
	}

	// ============ NEW METHODS FOR FRONTEND INTEGRATION ============

	public List<Order> getRecentOrdersWithLimit(int limit) {
		return orderRepository.findTop10ByOrderByOrderDateDesc().stream().limit(limit).collect(Collectors.toList());
	}

	public List<Book> getTopSellingBooksWithLimit(int limit) {
		// Get best selling books and convert to Book objects
		List<Object[]> bestSellingData = orderItemRepository.findBestSellingBooks();
		return bestSellingData.stream().limit(limit).map(data -> {
			Long bookId = (Long) data[0];
			return bookRepository.findById(bookId).orElse(null);
		}).filter(book -> book != null).collect(Collectors.toList());
	}

	public Page<Book> getAllBooksWithFilters(String search, String category, Pageable pageable) {
		if (search != null && !search.trim().isEmpty()) {
			if (category != null && !category.trim().isEmpty()) {
				// Search with category filter
				return bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseAndCategoryName(search,
						search, category, pageable);
			} else {
				// Search only
				return bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(search, search,
						pageable);
			}
		} else if (category != null && !category.trim().isEmpty()) {
			// Category filter only
			return bookRepository.findByCategoryName(category, pageable);
		} else {
			// No filters
			return bookRepository.findAll(pageable);
		}
	}

	public boolean hasBookInOrders(Long bookId) {
		return orderItemRepository.existsByBookId(bookId);
	}

	public Map<String, Object> updateBookStock(Long bookId, String operation, Integer quantity, String reason) {
		Optional<Book> bookOpt = bookRepository.findById(bookId);
		if (!bookOpt.isPresent()) {
			throw new RuntimeException("Book not found");
		}

		Book book = bookOpt.get();
		Integer currentQuantity = book.getStockQuantity();
		Integer newQuantity;

		switch (operation.toLowerCase()) {
		case "set":
			newQuantity = quantity;
			break;
		case "add":
			newQuantity = currentQuantity + quantity;
			break;
		case "subtract":
			newQuantity = Math.max(0, currentQuantity - quantity);
			break;
		default:
			throw new RuntimeException("Invalid operation. Use 'set', 'add', or 'subtract'");
		}

		book.setStockQuantity(newQuantity);
		bookRepository.save(book);

		// Return result
		Map<String, Object> result = new HashMap<>();
		result.put("oldQuantity", currentQuantity);
		result.put("newQuantity", newQuantity);
		result.put("operation", operation);
		result.put("reason", reason);

		return result;
	}

	public Page<Order> getAllOrdersWithFilters(String status, String search, String dateRange, Pageable pageable) {
		LocalDateTime startDate = null;

		// Handle date range
		if (dateRange != null) {
			LocalDateTime now = LocalDateTime.now();
			switch (dateRange.toLowerCase()) {
			case "today":
				startDate = now.toLocalDate().atStartOfDay();
				break;
			case "week":
				startDate = now.minusWeeks(1);
				break;
			case "month":
				startDate = now.minusMonths(1);
				break;
			}
		}

		// Apply filters
		if (search != null && !search.trim().isEmpty()) {
			if (status != null && !status.trim().isEmpty()) {
				if (startDate != null) {
					return orderRepository
							.findByStatusAndUserFirstNameContainingIgnoreCaseOrUserLastNameContainingIgnoreCaseAndOrderDateAfter(
									Order.OrderStatus.valueOf(status.toUpperCase()), search, search, startDate,
									pageable);
				} else {
					return orderRepository
							.findByStatusAndUserFirstNameContainingIgnoreCaseOrUserLastNameContainingIgnoreCase(
									Order.OrderStatus.valueOf(status.toUpperCase()), search, search, pageable);
				}
			} else {
				if (startDate != null) {
					return orderRepository
							.findByUserFirstNameContainingIgnoreCaseOrUserLastNameContainingIgnoreCaseAndOrderDateAfter(
									search, search, startDate, pageable);
				} else {
					return orderRepository.findByUserFirstNameContainingIgnoreCaseOrUserLastNameContainingIgnoreCase(
							search, search, pageable);
				}
			}
		} else if (status != null && !status.trim().isEmpty()) {
			if (startDate != null) {
				return orderRepository.findByStatusAndOrderDateAfter(Order.OrderStatus.valueOf(status.toUpperCase()),
						startDate, pageable);
			} else {
				return orderRepository.findByStatus(Order.OrderStatus.valueOf(status.toUpperCase()), pageable);
			}
		} else if (startDate != null) {
			return orderRepository.findByOrderDateAfter(startDate, pageable);
		} else {
			return orderRepository.findAll(pageable);
		}
	}

	public boolean updateOrderStatus(Long orderId, String status, String note) {
		Optional<Order> orderOpt = orderRepository.findById(orderId);
		if (!orderOpt.isPresent()) {
			return false;
		}

		Order order = orderOpt.get();
		try {
			Order.OrderStatus newStatus = Order.OrderStatus.valueOf(status.toUpperCase());
			order.setStatus(newStatus);
			orderRepository.save(order);

			// You can log the status change here if needed
			// logOrderStatusChange(orderId, order.getStatus(), newStatus, note);

			return true;
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("Invalid status: " + status);
		}
	}

	public Page<User> getAllUsersWithFilters(String search, String status, String usertype, Pageable pageable) {
		if (search != null && !search.trim().isEmpty()) {
			if (status != null && !status.trim().isEmpty()) {
				if (usertype != null && !usertype.trim().isEmpty()) {
					User.Role role = "admin".equalsIgnoreCase(usertype) ? User.Role.ADMIN : User.Role.USER;
					return userRepository
							.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCaseAndRoleAndStatus(
									search, search, search, role, status, pageable);
				} else {
					return userRepository
							.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCaseAndStatus(
									search, search, search, status, pageable);
				}
			} else if (usertype != null && !usertype.trim().isEmpty()) {
				User.Role role = "admin".equalsIgnoreCase(usertype) ? User.Role.ADMIN : User.Role.USER;
				return userRepository
						.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCaseAndRole(
								search, search, search, role, pageable);
			} else {
				return userRepository
						.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
								search, search, search, pageable);
			}
		} else if (status != null && !status.trim().isEmpty()) {
			if (usertype != null && !usertype.trim().isEmpty()) {
				User.Role role = "admin".equalsIgnoreCase(usertype) ? User.Role.ADMIN : User.Role.USER;
				return userRepository.findByRoleAndStatus(role, status, pageable);
			} else {
				return userRepository.findByStatus(status, pageable);
			}
		} else if (usertype != null && !usertype.trim().isEmpty()) {
			User.Role role = "admin".equalsIgnoreCase(usertype) ? User.Role.ADMIN : User.Role.USER;
			return userRepository.findByRole(role, pageable);
		} else {
			return userRepository.findAll(pageable);
		}
	}

	public boolean updateUserStatus(Long userId, String status) {
		Optional<User> userOpt = userRepository.findById(userId);
		if (!userOpt.isPresent()) {
			return false;
		}

		User user = userOpt.get();
		// Don't allow changing admin user status
		if (user.getRole() == User.Role.ADMIN) {
			return false;
		}

		user.setStatus(status);
		userRepository.save(user);
		return true;
	}

	public List<Order> getUserOrders(Long userId, int limit) {
		return orderRepository.findByUserIdOrderByOrderDateDesc(userId).stream().limit(limit)
				.collect(Collectors.toList());
	}

	public List<Map<String, Object>> getAllCategories() {
		// Get distinct categories from books
		List<String> categoryNames = bookRepository.findDistinctCategories();

		List<Map<String, Object>> categories = new ArrayList<>();
		for (int i = 0; i < categoryNames.size(); i++) {
			Map<String, Object> category = new HashMap<>();
			category.put("id", i + 1);
			category.put("name", categoryNames.get(i));
			categories.add(category);
		}

		return categories;
	}

	public Map<String, Object> createCategory(String name) {
		// Since categories are derived from books, we just return the category info
		// In a real implementation, you might have a separate Category entity
		Map<String, Object> category = new HashMap<>();
		category.put("id", System.currentTimeMillis()); // Simple ID generation
		category.put("name", name);

		return category;
	}

	// Additional utility methods

	public List<Book> getBooksLowStock(int threshold) {
		return bookRepository.findByStockQuantityLessThan(threshold);
	}

	public List<Book> getBooksOutOfStock() {
		return bookRepository.findByStockQuantity(0);
	}

	public Long getTotalCustomers() {
		return userRepository.countByRole(User.Role.USER);
	}

	public Long getTotalActiveUsers() {
		return userRepository.countByRoleAndStatus(User.Role.USER, "active");
	}

	public BigDecimal getTotalRevenueForPeriod(String period) {
		LocalDateTime startDate;
		LocalDateTime now = LocalDateTime.now();

		switch (period.toLowerCase()) {
		case "today":
			startDate = now.toLocalDate().atStartOfDay();
			break;
		case "week":
			startDate = now.minusWeeks(1);
			break;
		case "month":
			startDate = now.minusMonths(1);
			break;
		default:
			startDate = LocalDateTime.of(1970, 1, 1, 0, 0);
		}

		return orderRepository.getTotalRevenueByStatusAndDateRange(Order.OrderStatus.DELIVERED, startDate, now);
	}

	public Long getOrderCountForPeriod(String period) {
		LocalDateTime startDate;
		LocalDateTime now = LocalDateTime.now();

		switch (period.toLowerCase()) {
		case "today":
			startDate = now.toLocalDate().atStartOfDay();
			break;
		case "week":
			startDate = now.minusWeeks(1);
			break;
		case "month":
			startDate = now.minusMonths(1);
			break;
		default:
			startDate = LocalDateTime.of(1970, 1, 1, 0, 0);
		}

		return orderRepository.countOrdersBetweenDates(startDate, now);
	}
}
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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileWriter;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
}
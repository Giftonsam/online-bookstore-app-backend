// controller/AdminController.java
package com.bookstore.backend.controller;

import com.bookstore.backend.dto.AdminDto;
import com.bookstore.backend.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

	@Autowired
	private AdminService adminService;

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
}
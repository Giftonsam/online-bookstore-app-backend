// service/BookService.java
package com.bookstore.backend.service;

import com.bookstore.backend.entity.Book;
import com.bookstore.backend.entity.Category;
import com.bookstore.backend.repository.BookRepository;
import com.bookstore.backend.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class BookService {

	@Autowired
	private BookRepository bookRepository;

	private final String uploadDir = "uploads/books/";

	public Page<Book> getAllBooks(int page, int size, String sortBy, String sortDirection) {
		Sort sort = sortDirection.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
		Pageable pageable = PageRequest.of(page, size, sort);
		return bookRepository.findByIsActiveTrue(pageable);
	}

	public Book getBookById(Long id) {
		return bookRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
	}

	public Book createBook(Book book, MultipartFile image) {
		if (image != null && !image.isEmpty()) {
			String imageUrl = saveImage(image);
			book.setImageUrl(imageUrl);
		}
		return bookRepository.save(book);
	}

	public Book updateBook(Long id, Book bookDetails, MultipartFile image) {
		Book book = getBookById(id);

		book.setTitle(bookDetails.getTitle());
		book.setAuthor(bookDetails.getAuthor());
		book.setIsbn(bookDetails.getIsbn());
		book.setDescription(bookDetails.getDescription());
		book.setPrice(bookDetails.getPrice());
		book.setDiscountPrice(bookDetails.getDiscountPrice());
		book.setStockQuantity(bookDetails.getStockQuantity());
		book.setPublicationDate(bookDetails.getPublicationDate());
		book.setPublisher(bookDetails.getPublisher());
		book.setPages(bookDetails.getPages());
		book.setLanguage(bookDetails.getLanguage());
		book.setIsFeatured(bookDetails.getIsFeatured());
		book.setCategory(bookDetails.getCategory());

		if (image != null && !image.isEmpty()) {
			String imageUrl = saveImage(image);
			book.setImageUrl(imageUrl);
		}

		return bookRepository.save(book);
	}

	public void deleteBook(Long id) {
		Book book = getBookById(id);
		book.setIsActive(false);
		bookRepository.save(book);
	}

	public Page<Book> getBooksByCategory(Category category, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return bookRepository.findByCategoryAndIsActiveTrue(category, pageable);
	}

	public Page<Book> getFeaturedBooks(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return bookRepository.findByIsFeaturedTrueAndIsActiveTrue(pageable);
	}

	public Page<Book> searchBooks(String keyword, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return bookRepository.searchBooks(keyword, pageable);
	}

	public Page<Book> getBooksByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return bookRepository.findByPriceRange(minPrice, maxPrice, pageable);
	}

	public List<Book> getLowStockBooks() {
		return bookRepository.findByStockQuantityLessThan(10);
	}

	public List<Book> getLatestBooks() {
		return bookRepository.findTop10ByIsActiveTrueOrderByCreatedAtDesc();
	}

	public void updateStock(Long bookId, Integer quantity) {
		Book book = getBookById(bookId);
		book.setStockQuantity(book.getStockQuantity() - quantity);
		bookRepository.save(book);
	}

	private String saveImage(MultipartFile image) {
		try {
			// Create upload directory if it doesn't exist
			Path uploadPath = Paths.get(uploadDir);
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}

			// Generate unique filename
			String filename = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
			Path filePath = uploadPath.resolve(filename);

			// Save file
			Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

			return "/uploads/books/" + filename;
		} catch (IOException e) {
			throw new RuntimeException("Failed to save image: " + e.getMessage());
		}
	}

	public Long countActiveBooks() {
		return bookRepository.countActiveBooks();
	}

	public Long countLowStockBooks() {
		return bookRepository.countLowStockBooks(10);
	}

	public Optional<Book> findById(Long id) {
		return bookRepository.findById(id);
	}

	public Book save(Book book) {
		return bookRepository.save(book);
	}

	public void deleteById(Long id) {
		bookRepository.deleteById(id);
	}
}
// controller/BookController.java
package com.bookstore.backend.controller;

import com.bookstore.backend.dto.BookDto;
import com.bookstore.backend.entity.Book;
import com.bookstore.backend.entity.Category;
import com.bookstore.backend.service.BookService;
import com.bookstore.backend.service.CategoryService;
import com.bookstore.backend.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "*", maxAge = 3600)
public class BookController {

	@Autowired
	private BookService bookService;

	@Autowired
	private CategoryService categoryService;

	@GetMapping
	public ResponseEntity<Page<Book>> getAllBooks(@RequestParam(defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
			@RequestParam(defaultValue = Constants.DEFAULT_PAGE_SIZE) int size,
			@RequestParam(defaultValue = Constants.DEFAULT_SORT_BY) String sortBy,
			@RequestParam(defaultValue = Constants.DEFAULT_SORT_DIRECTION) String sortDir) {

		Page<Book> books = bookService.getAllBooks(page, size, sortBy, sortDir);
		return ResponseEntity.ok(books);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Book> getBookById(@PathVariable Long id) {
		Book book = bookService.getBookById(id);
		return ResponseEntity.ok(book);
	}

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Book> createBook(@Valid @RequestBody BookDto.CreateBookRequest request,
			@RequestParam(required = false) MultipartFile image) {

		Book book = new Book();
		book.setTitle(request.getTitle());
		book.setAuthor(request.getAuthor());
		book.setIsbn(request.getIsbn());
		book.setDescription(request.getDescription());
		book.setPrice(request.getPrice());
		book.setDiscountPrice(request.getDiscountPrice());
		book.setStockQuantity(request.getStockQuantity());
		book.setPublicationDate(request.getPublicationDate());
		book.setPublisher(request.getPublisher());
		book.setPages(request.getPages());
		book.setLanguage(request.getLanguage());
		book.setIsFeatured(request.getIsFeatured());

		if (request.getCategoryId() != null) {
			Category category = categoryService.getCategoryById(request.getCategoryId());
			book.setCategory(category);
		}

		Book savedBook = bookService.createBook(book, image);
		return ResponseEntity.ok(savedBook);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Book> updateBook(@PathVariable Long id, @Valid @RequestBody BookDto.UpdateBookRequest request,
			@RequestParam(required = false) MultipartFile image) {

		Book book = new Book();
		book.setTitle(request.getTitle());
		book.setAuthor(request.getAuthor());
		book.setIsbn(request.getIsbn());
		book.setDescription(request.getDescription());
		book.setPrice(request.getPrice());
		book.setDiscountPrice(request.getDiscountPrice());
		book.setStockQuantity(request.getStockQuantity());
		book.setPublicationDate(request.getPublicationDate());
		book.setPublisher(request.getPublisher());
		book.setPages(request.getPages());
		book.setLanguage(request.getLanguage());
		book.setIsFeatured(request.getIsFeatured());

		if (request.getCategoryId() != null) {
			Category category = categoryService.getCategoryById(request.getCategoryId());
			book.setCategory(category);
		}

		Book updatedBook = bookService.updateBook(id, book, image);
		return ResponseEntity.ok(updatedBook);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> deleteBook(@PathVariable Long id) {
		bookService.deleteBook(id);
		return ResponseEntity.ok(new BookDto.MessageResponse("Book deleted successfully!"));
	}

	@GetMapping("/search")
	public ResponseEntity<Page<Book>> searchBooks(@RequestParam String keyword,
			@RequestParam(defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
			@RequestParam(defaultValue = Constants.DEFAULT_PAGE_SIZE) int size) {

		Page<Book> books = bookService.searchBooks(keyword, page, size);
		return ResponseEntity.ok(books);
	}

	@GetMapping("/category/{categoryId}")
	public ResponseEntity<Page<Book>> getBooksByCategory(@PathVariable Long categoryId,
			@RequestParam(defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
			@RequestParam(defaultValue = Constants.DEFAULT_PAGE_SIZE) int size) {

		Category category = categoryService.getCategoryById(categoryId);
		Page<Book> books = bookService.getBooksByCategory(category, page, size);
		return ResponseEntity.ok(books);
	}

	@GetMapping("/featured")
	public ResponseEntity<Page<Book>> getFeaturedBooks(
			@RequestParam(defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
			@RequestParam(defaultValue = Constants.DEFAULT_PAGE_SIZE) int size) {

		Page<Book> books = bookService.getFeaturedBooks(page, size);
		return ResponseEntity.ok(books);
	}

	@GetMapping("/latest")
	public ResponseEntity<List<Book>> getLatestBooks() {
		List<Book> books = bookService.getLatestBooks();
		return ResponseEntity.ok(books);
	}

	@GetMapping("/price-range")
	public ResponseEntity<Page<Book>> getBooksByPriceRange(@RequestParam BigDecimal minPrice,
			@RequestParam BigDecimal maxPrice, @RequestParam(defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
			@RequestParam(defaultValue = Constants.DEFAULT_PAGE_SIZE) int size) {

		Page<Book> books = bookService.getBooksByPriceRange(minPrice, maxPrice, page, size);
		return ResponseEntity.ok(books);
	}
}
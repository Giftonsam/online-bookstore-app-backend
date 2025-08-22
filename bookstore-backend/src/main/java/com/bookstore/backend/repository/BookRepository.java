// repository/BookRepository.java
package com.bookstore.backend.repository;

import com.bookstore.backend.entity.Book;
import com.bookstore.backend.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

	Page<Book> findByIsActiveTrue(Pageable pageable);

	Page<Book> findByCategoryAndIsActiveTrue(Category category, Pageable pageable);

	Page<Book> findByIsFeaturedTrueAndIsActiveTrue(Pageable pageable);

	List<Book> findByStockQuantityLessThan(Integer threshold);

	Optional<Book> findByIsbn(String isbn);

	@Query("SELECT b FROM Book b WHERE b.isActive = true AND "
			+ "(LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
			+ "LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
			+ "LOWER(b.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
	Page<Book> searchBooks(@Param("keyword") String keyword, Pageable pageable);

	@Query("SELECT b FROM Book b WHERE b.isActive = true AND " + "b.price BETWEEN :minPrice AND :maxPrice")
	Page<Book> findByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice,
			Pageable pageable);

	@Query("SELECT b FROM Book b WHERE b.category = :category AND b.isActive = true AND "
			+ "b.price BETWEEN :minPrice AND :maxPrice")
	Page<Book> findByCategoryAndPriceRange(@Param("category") Category category, @Param("minPrice") BigDecimal minPrice,
			@Param("maxPrice") BigDecimal maxPrice, Pageable pageable);

	@Query("SELECT COUNT(b) FROM Book b WHERE b.isActive = true")
	Long countActiveBooks();

	@Query("SELECT COUNT(b) FROM Book b WHERE b.stockQuantity < :threshold")
	Long countLowStockBooks(@Param("threshold") Integer threshold);

	List<Book> findTop10ByIsActiveTrueOrderByCreatedAtDesc();

	// ============ BookRepository.java ============
	// Add these methods to your existing BookRepository interface

	@Query("SELECT DISTINCT b.category.name FROM Book b WHERE b.category.name IS NOT NULL ORDER BY b.category.name")
	List<String> findDistinctCategories();

	Page<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(String title, String author,
			Pageable pageable);

	Page<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseAndCategoryName(String title, String author,
			String categoryName, Pageable pageable);

	Page<Book> findByCategoryName(String categoryName, Pageable pageable);

	List<Book> findByStockQuantity(Integer stockQuantity);
}
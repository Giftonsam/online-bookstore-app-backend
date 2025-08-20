// repository/CategoryRepository.java
package com.bookstore.backend.repository;

import com.bookstore.backend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

	List<Category> findByIsActiveTrueOrderByNameAsc();

	Optional<Category> findByNameAndIsActiveTrue(String name);

	Boolean existsByName(String name);

	@Query("SELECT c FROM Category c WHERE c.isActive = true AND "
			+ "LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
	List<Category> searchCategories(@Param("keyword") String keyword);

	@Query("SELECT COUNT(c) FROM Category c WHERE c.isActive = true")
	Long countActiveCategories();
}
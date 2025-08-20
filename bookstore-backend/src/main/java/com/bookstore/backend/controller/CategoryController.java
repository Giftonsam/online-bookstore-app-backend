// controller/CategoryController.java
package com.bookstore.backend.controller;

import com.bookstore.backend.dto.CategoryDto;
import com.bookstore.backend.entity.Category;
import com.bookstore.backend.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CategoryController {

	@Autowired
	private CategoryService categoryService;

	@GetMapping
	public ResponseEntity<List<Category>> getAllCategories() {
		List<Category> categories = categoryService.getAllCategories();
		return ResponseEntity.ok(categories);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
		Category category = categoryService.getCategoryById(id);
		return ResponseEntity.ok(category);
	}

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Category> createCategory(@Valid @RequestBody CategoryDto.CreateCategoryRequest request) {
		Category category = new Category();
		category.setName(request.getName());
		category.setDescription(request.getDescription());

		Category savedCategory = categoryService.createCategory(category);
		return ResponseEntity.ok(savedCategory);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Category> updateCategory(@PathVariable Long id,
			@Valid @RequestBody CategoryDto.UpdateCategoryRequest request) {

		Category category = new Category();
		category.setName(request.getName());
		category.setDescription(request.getDescription());

		Category updatedCategory = categoryService.updateCategory(id, category);
		return ResponseEntity.ok(updatedCategory);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
		categoryService.deleteCategory(id);
		return ResponseEntity.ok(new CategoryDto.MessageResponse("Category deleted successfully!"));
	}

	@GetMapping("/search")
	public ResponseEntity<List<Category>> searchCategories(@RequestParam String keyword) {
		List<Category> categories = categoryService.searchCategories(keyword);
		return ResponseEntity.ok(categories);
	}
}
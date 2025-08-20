// service/CategoryService.java
package com.bookstore.backend.service;

import com.bookstore.backend.entity.Category;
import com.bookstore.backend.repository.CategoryRepository;
import com.bookstore.backend.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CategoryService {

	@Autowired
	private CategoryRepository categoryRepository;

	public List<Category> getAllCategories() {
		return categoryRepository.findByIsActiveTrueOrderByNameAsc();
	}

	public Category getCategoryById(Long id) {
		return categoryRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
	}

	public Category createCategory(Category category) {
		if (categoryRepository.existsByName(category.getName())) {
			throw new RuntimeException("Category with name '" + category.getName() + "' already exists!");
		}
		return categoryRepository.save(category);
	}

	public Category updateCategory(Long id, Category categoryDetails) {
		Category category = getCategoryById(id);

		// Check if name is being changed and if it already exists
		if (!category.getName().equals(categoryDetails.getName())
				&& categoryRepository.existsByName(categoryDetails.getName())) {
			throw new RuntimeException("Category with name '" + categoryDetails.getName() + "' already exists!");
		}

		category.setName(categoryDetails.getName());
		category.setDescription(categoryDetails.getDescription());

		return categoryRepository.save(category);
	}

	public void deleteCategory(Long id) {
		Category category = getCategoryById(id);
		category.setIsActive(false);
		categoryRepository.save(category);
	}

	public List<Category> searchCategories(String keyword) {
		return categoryRepository.searchCategories(keyword);
	}

	public Category getCategoryByName(String name) {
		return categoryRepository.findByNameAndIsActiveTrue(name)
				.orElseThrow(() -> new ResourceNotFoundException("Category not found with name: " + name));
	}

	public Long countActiveCategories() {
		return categoryRepository.countActiveCategories();
	}
}
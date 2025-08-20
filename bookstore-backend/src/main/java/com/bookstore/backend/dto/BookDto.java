// dto/BookDto.java
package com.bookstore.backend.dto;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public class BookDto {

	public static class CreateBookRequest {
		@NotBlank
		@Size(max = 200)
		private String title;

		@NotBlank
		@Size(max = 100)
		private String author;

		@Size(max = 20)
		private String isbn;

		private String description;

		@NotNull
		@DecimalMin(value = "0.0", inclusive = false)
		private BigDecimal price;

		@DecimalMin(value = "0.0")
		private BigDecimal discountPrice;

		@NotNull
		private Integer stockQuantity;

		private LocalDate publicationDate;

		@Size(max = 100)
		private String publisher;

		@NotNull
		private Integer pages;

		@Size(max = 50)
		private String language = "English";

		private Boolean isFeatured = false;

		private Long categoryId;

		// Getters and Setters
		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getAuthor() {
			return author;
		}

		public void setAuthor(String author) {
			this.author = author;
		}

		public String getIsbn() {
			return isbn;
		}

		public void setIsbn(String isbn) {
			this.isbn = isbn;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public BigDecimal getPrice() {
			return price;
		}

		public void setPrice(BigDecimal price) {
			this.price = price;
		}

		public BigDecimal getDiscountPrice() {
			return discountPrice;
		}

		public void setDiscountPrice(BigDecimal discountPrice) {
			this.discountPrice = discountPrice;
		}

		public Integer getStockQuantity() {
			return stockQuantity;
		}

		public void setStockQuantity(Integer stockQuantity) {
			this.stockQuantity = stockQuantity;
		}

		public LocalDate getPublicationDate() {
			return publicationDate;
		}

		public void setPublicationDate(LocalDate publicationDate) {
			this.publicationDate = publicationDate;
		}

		public String getPublisher() {
			return publisher;
		}

		public void setPublisher(String publisher) {
			this.publisher = publisher;
		}

		public Integer getPages() {
			return pages;
		}

		public void setPages(Integer pages) {
			this.pages = pages;
		}

		public String getLanguage() {
			return language;
		}

		public void setLanguage(String language) {
			this.language = language;
		}

		public Boolean getIsFeatured() {
			return isFeatured;
		}

		public void setIsFeatured(Boolean isFeatured) {
			this.isFeatured = isFeatured;
		}

		public Long getCategoryId() {
			return categoryId;
		}

		public void setCategoryId(Long categoryId) {
			this.categoryId = categoryId;
		}
	}

	public static class UpdateBookRequest extends CreateBookRequest {
		// Inherits all fields from CreateBookRequest
	}

	public static class MessageResponse {
		private String message;

		public MessageResponse(String message) {
			this.message = message;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
	}
}
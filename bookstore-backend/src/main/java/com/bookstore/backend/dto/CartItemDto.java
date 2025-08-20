// dto/CartItemDto.java
package com.bookstore.backend.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class CartItemDto {

	public static class AddToCartRequest {
		@NotNull
		private Long bookId;

		@NotNull
		@Min(1)
		private Integer quantity;

		// Getters and Setters
		public Long getBookId() {
			return bookId;
		}

		public void setBookId(Long bookId) {
			this.bookId = bookId;
		}

		public Integer getQuantity() {
			return quantity;
		}

		public void setQuantity(Integer quantity) {
			this.quantity = quantity;
		}
	}

	public static class UpdateCartItemRequest {
		@NotNull
		private Long bookId;

		@NotNull
		@Min(0)
		private Integer quantity;

		// Getters and Setters
		public Long getBookId() {
			return bookId;
		}

		public void setBookId(Long bookId) {
			this.bookId = bookId;
		}

		public Integer getQuantity() {
			return quantity;
		}

		public void setQuantity(Integer quantity) {
			this.quantity = quantity;
		}
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
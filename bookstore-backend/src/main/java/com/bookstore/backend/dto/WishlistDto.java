// dto/WishlistDto.java
package com.bookstore.backend.dto;

import javax.validation.constraints.NotNull;

public class WishlistDto {

	public static class AddToWishlistRequest {
		@NotNull
		private Long bookId;

		// Getters and Setters
		public Long getBookId() {
			return bookId;
		}

		public void setBookId(Long bookId) {
			this.bookId = bookId;
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
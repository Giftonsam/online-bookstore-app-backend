// dto/CategoryDto.java
package com.bookstore.backend.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class CategoryDto {

	public static class CreateCategoryRequest {
		@NotBlank
		@Size(max = 100)
		private String name;

		@Size(max = 500)
		private String description;

		// Getters and Setters
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}
	}

	public static class UpdateCategoryRequest extends CreateCategoryRequest {
		// Inherits all fields from CreateCategoryRequest
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
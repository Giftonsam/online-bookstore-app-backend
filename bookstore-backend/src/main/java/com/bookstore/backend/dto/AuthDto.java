// dto/AuthDto.java
package com.bookstore.backend.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class AuthDto {

	public static class LoginRequest {
		@NotBlank
		@Email
		private String email;

		@NotBlank
		private String password;

		// Getters and Setters
		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}
	}

	public static class RegisterRequest {
		@NotBlank
		@Size(max = 50)
		private String firstName;

		@NotBlank
		@Size(max = 50)
		private String lastName;

		@NotBlank
		@Email
		@Size(max = 100)
		private String email;

		@NotBlank
		@Size(min = 6, max = 120)
		private String password;

		@Size(max = 15)
		private String phone;

		private String address;

		// Getters and Setters
		public String getFirstName() {
			return firstName;
		}

		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}

		public String getLastName() {
			return lastName;
		}

		public void setLastName(String lastName) {
			this.lastName = lastName;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getPhone() {
			return phone;
		}

		public void setPhone(String phone) {
			this.phone = phone;
		}

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
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
// dto/PaymentDto.java
package com.bookstore.backend.dto;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class PaymentDto {

	public static class CreatePaymentRequest {
		@NotNull
		@DecimalMin(value = "0.0", inclusive = false)
		private BigDecimal amount;

		// Getters and Setters
		public BigDecimal getAmount() {
			return amount;
		}

		public void setAmount(BigDecimal amount) {
			this.amount = amount;
		}
	}

	public static class VerifyPaymentRequest {
		@NotBlank
		private String razorpayOrderId;

		@NotBlank
		private String razorpayPaymentId;

		@NotBlank
		private String razorpaySignature;

		// Getters and Setters
		public String getRazorpayOrderId() {
			return razorpayOrderId;
		}

		public void setRazorpayOrderId(String razorpayOrderId) {
			this.razorpayOrderId = razorpayOrderId;
		}

		public String getRazorpayPaymentId() {
			return razorpayPaymentId;
		}

		public void setRazorpayPaymentId(String razorpayPaymentId) {
			this.razorpayPaymentId = razorpayPaymentId;
		}

		public String getRazorpaySignature() {
			return razorpaySignature;
		}

		public void setRazorpaySignature(String razorpaySignature) {
			this.razorpaySignature = razorpaySignature;
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
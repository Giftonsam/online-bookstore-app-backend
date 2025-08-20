// dto/OrderDto.java
package com.bookstore.backend.dto;

import com.bookstore.backend.entity.Order;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class OrderDto {

	public static class CreateOrderRequest {
		@NotBlank
		private String shippingAddress;

		// Getters and Setters
		public String getShippingAddress() {
			return shippingAddress;
		}

		public void setShippingAddress(String shippingAddress) {
			this.shippingAddress = shippingAddress;
		}
	}

	public static class UpdateOrderStatusRequest {
		@NotNull
		private Order.OrderStatus status;

		// Getters and Setters
		public Order.OrderStatus getStatus() {
			return status;
		}

		public void setStatus(Order.OrderStatus status) {
			this.status = status;
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
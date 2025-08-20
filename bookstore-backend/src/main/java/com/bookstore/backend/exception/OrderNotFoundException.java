// exception/OrderNotFoundException.java
package com.bookstore.backend.exception;

public class OrderNotFoundException extends CustomException {
	public OrderNotFoundException(String message) {
		super("ORDER_NOT_FOUND", message);
	}
}
// exception/InsufficientStockException.java
package com.bookstore.backend.exception;

public class InsufficientStockException extends CustomException {
	public InsufficientStockException(String message) {
		super("INSUFFICIENT_STOCK", message);
	}
}
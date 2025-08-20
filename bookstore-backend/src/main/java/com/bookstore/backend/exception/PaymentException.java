// exception/PaymentException.java
package com.bookstore.backend.exception;

public class PaymentException extends CustomException {
	public PaymentException(String message) {
		super("PAYMENT_ERROR", message);
	}

	public PaymentException(String message, Throwable cause) {
		super("PAYMENT_ERROR", message, cause);
	}
}
// exception/CustomException.java
package com.bookstore.backend.exception;

public class CustomException extends RuntimeException {

	private String errorCode;
	private String errorMessage;

	public CustomException(String message) {
		super(message);
		this.errorMessage = message;
	}

	public CustomException(String message, Throwable cause) {
		super(message, cause);
		this.errorMessage = message;
	}

	public CustomException(String errorCode, String errorMessage) {
		super(errorMessage);
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	public CustomException(String errorCode, String errorMessage, Throwable cause) {
		super(errorMessage, cause);
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	// Getters
	public String getErrorCode() {
		return errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	// Setters
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
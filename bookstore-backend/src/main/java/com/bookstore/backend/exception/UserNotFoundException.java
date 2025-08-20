// exception/UserNotFoundException.java
package com.bookstore.backend.exception;

public class UserNotFoundException extends CustomException {
	public UserNotFoundException(String message) {
		super("USER_NOT_FOUND", message);
	}
}
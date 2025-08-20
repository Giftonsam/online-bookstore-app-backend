// exception/BookNotFoundException.java
package com.bookstore.backend.exception;

public class BookNotFoundException extends CustomException {
	public BookNotFoundException(String message) {
		super("BOOK_NOT_FOUND", message);
	}
}
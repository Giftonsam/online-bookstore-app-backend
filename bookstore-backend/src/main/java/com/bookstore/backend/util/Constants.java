// util/Constants.java
package com.bookstore.backend.util;

public class Constants {

	// Pagination
	public static final String DEFAULT_PAGE_NUMBER = "0";
	public static final String DEFAULT_PAGE_SIZE = "12";
	public static final String DEFAULT_SORT_BY = "id";
	public static final String DEFAULT_SORT_DIRECTION = "asc";

	// File Upload
	public static final String UPLOAD_DIR = "uploads/";
	public static final String BOOK_IMAGES_DIR = "uploads/books/";
	public static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

	// Order Status
	public static final String ORDER_PENDING = "PENDING";
	public static final String ORDER_CONFIRMED = "CONFIRMED";
	public static final String ORDER_SHIPPED = "SHIPPED";
	public static final String ORDER_DELIVERED = "DELIVERED";

	// Payment Status
	public static final String PAYMENT_SUCCESS = "SUCCESS";
	public static final String PAYMENT_FAILED = "FAILED";
	public static final String PAYMENT_PENDING = "PENDING";

	// Email Templates
	public static final String ORDER_CONFIRMATION_SUBJECT = "Order Confirmation - Bookstore";
	public static final String ORDER_SHIPPED_SUBJECT = "Your Order has been Shipped - Bookstore";
	public static final String WELCOME_SUBJECT = "Welcome to Bookstore";

	// Admin Settings
	public static final int LOW_STOCK_THRESHOLD = 10;
	public static final String DEFAULT_ADMIN_EMAIL = "admin@bookstore.com";

	// API Response Messages
	public static final String SUCCESS_MESSAGE = "Operation completed successfully";
	public static final String ERROR_MESSAGE = "An error occurred while processing your request";
	public static final String NOT_FOUND_MESSAGE = "Resource not found";
	public static final String UNAUTHORIZED_MESSAGE = "Unauthorized access";
	public static final String FORBIDDEN_MESSAGE = "Access forbidden";

	// Razorpay
	public static final String RAZORPAY_CURRENCY = "INR";
	public static final String RAZORPAY_COMPANY_NAME = "Bookstore";
}
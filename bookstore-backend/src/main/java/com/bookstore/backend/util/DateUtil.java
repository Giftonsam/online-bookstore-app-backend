// util/DateUtil.java
package com.bookstore.backend.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {

	private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

	public static String formatDateTime(LocalDateTime dateTime) {
		return dateTime != null ? dateTime.format(DEFAULT_FORMATTER) : null;
	}

	public static String formatDate(LocalDateTime dateTime) {
		return dateTime != null ? dateTime.format(DATE_FORMATTER) : null;
	}

	public static String formatTime(LocalDateTime dateTime) {
		return dateTime != null ? dateTime.format(TIME_FORMATTER) : null;
	}

	public static LocalDateTime parseDateTime(String dateTimeStr) {
		return dateTimeStr != null ? LocalDateTime.parse(dateTimeStr, DEFAULT_FORMATTER) : null;
	}

	public static LocalDateTime getStartOfDay(LocalDateTime dateTime) {
		return dateTime.toLocalDate().atStartOfDay();
	}

	public static LocalDateTime getEndOfDay(LocalDateTime dateTime) {
		return dateTime.toLocalDate().atTime(23, 59, 59);
	}
}
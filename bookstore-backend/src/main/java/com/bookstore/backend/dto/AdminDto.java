// dto/AdminDto.java
package com.bookstore.backend.dto;

import java.time.LocalDateTime;

public class AdminDto {

	public static class ExportRequest {
		private LocalDateTime startDate;
		private LocalDateTime endDate;

		// Getters and Setters
		public LocalDateTime getStartDate() {
			return startDate;
		}

		public void setStartDate(LocalDateTime startDate) {
			this.startDate = startDate;
		}

		public LocalDateTime getEndDate() {
			return endDate;
		}

		public void setEndDate(LocalDateTime endDate) {
			this.endDate = endDate;
		}
	}

	public static class ExportResponse {
		private String message;
		private String filePath;

		public ExportResponse(String message, String filePath) {
			this.message = message;
			this.filePath = filePath;
		}

		// Getters and Setters
		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public String getFilePath() {
			return filePath;
		}

		public void setFilePath(String filePath) {
			this.filePath = filePath;
		}
	}

	public static class ImportResponse {
		private String message;
		private int importedCount;

		public ImportResponse(String message, int importedCount) {
			this.message = message;
			this.importedCount = importedCount;
		}

		// Getters and Setters
		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public int getImportedCount() {
			return importedCount;
		}

		public void setImportedCount(int importedCount) {
			this.importedCount = importedCount;
		}
	}
}
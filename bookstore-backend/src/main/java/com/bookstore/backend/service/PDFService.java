// service/PDFService.java
package com.bookstore.backend.service;

import com.bookstore.backend.entity.Order;
import com.bookstore.backend.entity.OrderItem;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;

@Service
public class PDFService {

	public byte[] generateOrderReceipt(Order order) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PdfWriter writer = new PdfWriter(baos);
			PdfDocument pdf = new PdfDocument(writer);
			Document document = new Document(pdf);

			// Title
			document.add(new Paragraph("ORDER RECEIPT").setFontSize(20).setBold());

			// Order details
			document.add(new Paragraph("Order Number: " + order.getOrderNumber()));
			document.add(
					new Paragraph("Customer: " + order.getUser().getFirstName() + " " + order.getUser().getLastName()));
			document.add(new Paragraph("Email: " + order.getUser().getEmail()));
			document.add(new Paragraph("Order Date: " + order.getOrderDate()));
			document.add(new Paragraph("Status: " + order.getStatus()));
			document.add(new Paragraph("\n"));

			// Order items table
			Table table = new Table(UnitValue.createPercentArray(new float[] { 3, 1, 1, 1 }));
			table.addHeaderCell("Book Title");
			table.addHeaderCell("Quantity");
			table.addHeaderCell("Unit Price");
			table.addHeaderCell("Total");

			BigDecimal grandTotal = BigDecimal.ZERO;
			for (OrderItem item : order.getOrderItems()) {
				table.addCell(item.getBook().getTitle());
				table.addCell(item.getQuantity().toString());
				table.addCell("₹" + item.getUnitPrice());
				table.addCell("₹" + item.getTotalPrice());
				grandTotal = grandTotal.add(item.getTotalPrice());
			}

			document.add(table);
			document.add(new Paragraph("\n"));
			document.add(new Paragraph("Grand Total: ₹" + grandTotal).setFontSize(14).setBold());

			// Shipping address
			document.add(new Paragraph("\nShipping Address:"));
			document.add(new Paragraph(order.getShippingAddress()));

			document.close();
			return baos.toByteArray();
		} catch (Exception e) {
			throw new RuntimeException("Failed to generate PDF: " + e.getMessage());
		}
	}
}
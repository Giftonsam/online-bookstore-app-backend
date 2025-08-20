// service/EmailService.java
package com.bookstore.backend.service;

import com.bookstore.backend.entity.Order;
import com.bookstore.backend.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender mailSender;

	@Value("${spring.mail.username}")
	private String fromEmail;

	public void sendOrderConfirmationEmail(Order order) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(fromEmail);
		message.setTo(order.getUser().getEmail());
		message.setSubject("Order Confirmation - " + order.getOrderNumber());

		String text = String.format(
				"Dear %s,\n\n" + "Thank you for your order!\n\n" + "Order Number: %s\n" + "Total Amount: ₹%.2f\n"
						+ "Order Date: %s\n\n" + "Your order will be processed soon.\n\n" + "Best regards,\n"
						+ "Bookstore Team",
				order.getUser().getFirstName(), order.getOrderNumber(), order.getTotalAmount(), order.getOrderDate());

		message.setText(text);
		mailSender.send(message);
	}

	public void sendOrderShippedEmail(Order order) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(fromEmail);
		message.setTo(order.getUser().getEmail());
		message.setSubject("Your Order has been Shipped - " + order.getOrderNumber());

		String text = String.format(
				"Dear %s,\n\n" + "Great news! Your order has been shipped.\n\n" + "Order Number: %s\n"
						+ "Shipping Address: %s\n\n" + "You should receive your books within 3-5 business days.\n\n"
						+ "Best regards,\n" + "Bookstore Team",
				order.getUser().getFirstName(), order.getOrderNumber(), order.getShippingAddress());

		message.setText(text);
		mailSender.send(message);
	}

	public void sendWelcomeEmail(User user) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(fromEmail);
		message.setTo(user.getEmail());
		message.setSubject("Welcome to Bookstore!");

		String text = String.format("Dear %s,\n\n" + "Welcome to our online bookstore!\n\n"
				+ "Thank you for creating an account. You can now:\n" + "- Browse our extensive collection of books\n"
				+ "- Add books to your cart and wishlist\n" + "- Place orders and track them\n"
				+ "- Manage your profile\n\n" + "Happy reading!\n\n" + "Best regards,\n" + "Bookstore Team",
				user.getFirstName());

		message.setText(text);
		mailSender.send(message);
	}
}
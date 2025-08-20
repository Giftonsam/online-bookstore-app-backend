// service/PaymentService.java
package com.bookstore.backend.service;

import com.bookstore.backend.entity.Payment;
import com.bookstore.backend.entity.Order;
import com.bookstore.backend.entity.User;
import com.bookstore.backend.repository.PaymentRepository;
import com.bookstore.backend.repository.OrderRepository;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class PaymentService {

	@Autowired
	private PaymentRepository paymentRepository;

	@Autowired
	private OrderRepository orderRepository;

	@Value("${razorpay.key.id}")
	private String razorpayKeyId;

	@Value("${razorpay.key.secret}")
	private String razorpayKeySecret;

	public Map<String, Object> createRazorpayOrder(BigDecimal amount, Long userId) {
		try {
			RazorpayClient razorpay = new RazorpayClient(razorpayKeyId, razorpayKeySecret);

			JSONObject orderRequest = new JSONObject();
			orderRequest.put("amount", amount.multiply(new BigDecimal(100)).intValue()); // Amount in paise
			orderRequest.put("currency", "INR");
			orderRequest.put("receipt", "order_rcptid_" + System.currentTimeMillis());

			com.razorpay.Order order = razorpay.orders.create(orderRequest);

			Map<String, Object> response = new HashMap<>();
			response.put("id", order.get("id"));
			response.put("amount", order.get("amount"));
			response.put("currency", order.get("currency"));
			response.put("key", razorpayKeyId);

			return response;
		} catch (RazorpayException e) {
			throw new RuntimeException("Failed to create Razorpay order: " + e.getMessage());
		}
	}

	public boolean verifyPayment(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) {
		try {
			String data = razorpayOrderId + "|" + razorpayPaymentId;
			String generatedSignature = calculateRFC2104HMAC(data, razorpayKeySecret);
			return generatedSignature.equals(razorpaySignature);
		} catch (Exception e) {
			return false;
		}
	}

	public void updatePaymentStatus(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) {
		Payment payment = paymentRepository.findByRazorpayOrderId(razorpayOrderId).orElse(new Payment());

		payment.setPaymentId(razorpayPaymentId);
		payment.setRazorpaySignature(razorpaySignature);
		payment.setStatus(Payment.PaymentStatus.SUCCESS);

		paymentRepository.save(payment);

		// Update order status
		if (payment.getOrder() != null) {
			Order order = payment.getOrder();
			order.setStatus(Order.OrderStatus.CONFIRMED);
			orderRepository.save(order);
		}
	}

	public void handleWebhook(Map<String, Object> payload) {
		// Handle Razorpay webhook events
		String event = (String) payload.get("event");

		if ("payment.captured".equals(event)) {
			Map<String, Object> paymentData = (Map<String, Object>) payload.get("payload");
			Map<String, Object> payment = (Map<String, Object>) paymentData.get("payment");

			String paymentId = (String) payment.get("id");
			String orderId = (String) payment.get("order_id");

			// Update payment status in database
			Payment dbPayment = paymentRepository.findByRazorpayOrderId(orderId).orElse(null);
			if (dbPayment != null) {
				dbPayment.setPaymentId(paymentId);
				dbPayment.setStatus(Payment.PaymentStatus.SUCCESS);
				paymentRepository.save(dbPayment);
			}
		}
	}

	private String calculateRFC2104HMAC(String data, String secret) throws Exception {
		Mac mac = Mac.getInstance("HmacSHA256");
		SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
		mac.init(secretKeySpec);
		byte[] digest = mac.doFinal(data.getBytes());
		return bytesToHex(digest);
	}

	private String bytesToHex(byte[] bytes) {
		StringBuilder result = new StringBuilder();
		for (byte b : bytes) {
			result.append(String.format("%02x", b));
		}
		return result.toString();
	}
}
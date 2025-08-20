// controller/PaymentController.java
package com.bookstore.backend.controller;

import com.bookstore.backend.dto.PaymentDto;
import com.bookstore.backend.service.PaymentService;
import com.bookstore.backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PaymentController {

	@Autowired
	private PaymentService paymentService;

	@Autowired
	private AuthService authService;

	@PostMapping("/create-order")
	public ResponseEntity<Map<String, Object>> createRazorpayOrder(
			@Valid @RequestBody PaymentDto.CreatePaymentRequest request) {
		Long userId = authService.getCurrentUser().getId();
		Map<String, Object> orderData = paymentService.createRazorpayOrder(request.getAmount(), userId);
		return ResponseEntity.ok(orderData);
	}

	@PostMapping("/verify")
	public ResponseEntity<?> verifyPayment(@Valid @RequestBody PaymentDto.VerifyPaymentRequest request) {
		boolean isValid = paymentService.verifyPayment(request.getRazorpayOrderId(), request.getRazorpayPaymentId(),
				request.getRazorpaySignature());

		if (isValid) {
			paymentService.updatePaymentStatus(request.getRazorpayOrderId(), request.getRazorpayPaymentId(),
					request.getRazorpaySignature());
			return ResponseEntity.ok(new PaymentDto.MessageResponse("Payment verified successfully!"));
		} else {
			return ResponseEntity.badRequest().body(new PaymentDto.MessageResponse("Payment verification failed!"));
		}
	}

	@PostMapping("/webhook")
	public ResponseEntity<?> handleWebhook(@RequestBody Map<String, Object> payload) {
		// Handle Razorpay webhooks for payment status updates
		paymentService.handleWebhook(payload);
		return ResponseEntity.ok().build();
	}
}
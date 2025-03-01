package com.orderservice.app.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.orderservice.app.entity.Cart;
import com.orderservice.app.entity.Items;
import com.orderservice.app.entity.Order;
//import com.orderservice.app.entity.Product;
//import com.orderservice.app.entity.UserProfile;
import com.orderservice.app.exception.OrderNotFoundException;
import com.orderservice.app.repository.OrderRepository;
//import com.orderservice.app.repository.ProfileRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

	@Autowired
	OrderRepository orderRepository;


	@Autowired
	private CartClient cartClient;

	@Override
	public ResponseEntity<Order> placeOrder(Order order) {
		if (cartClient.getCart(order.getCartId()) == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		double orderTotal = cartClient.getCart(order.getCartId()).getBody().getTotalPrice();
//		order.setAmountPaid(orderTotal);
		order.setAmountPaid(orderTotal - order.getAmountPaid());

		order.setOrderStatus("Order Placed");
		for (Items it : cartClient.getCart(order.getCartId()).getBody().getItems()) {
			order.addItem(it.getProductId());
		}
		cartClient.deleteCart(order.getCartId());

		// Logic to place order
		return new ResponseEntity<>(orderRepository.save(order), HttpStatusCode.valueOf(200));
	}

	@Override
	public ResponseEntity<List<Order>> getAllOrders() {
		List<Order> orders = orderRepository.findAll();
		if (orders.size() == 0)
			throw new OrderNotFoundException("No any data present");
		return new ResponseEntity<>(orders, HttpStatusCode.valueOf(200));
	}

	@Override
	public ResponseEntity<List<Order>> getOrderByCustomerId(int customerId) {
		// TODO Auto-generated method stub
		if (orderRepository.findByCustomerId(customerId).size() == 0)
			throw new OrderNotFoundException("No orders!! Shop !!");
		return new ResponseEntity<>(orderRepository.findByCustomerId(customerId), HttpStatusCode.valueOf(200));

	}

	@Override
	public ResponseEntity<Order> changeOrderStatus(String orderStatus, int orderId) {
		// TODO Auto-generated method stub
		Optional<Order> op = orderRepository.findById(orderId);
		Order order = null;
		if (!op.isEmpty()) {
			order = op.get();
			order.setOrderStatus(orderStatus);
			return new ResponseEntity<>(orderRepository.save(order), HttpStatusCode.valueOf(200));
		} else {
			throw new OrderNotFoundException("Order not found with ID " + orderId);
		}
	}

	@Override
	public ResponseEntity<String> deleteOrder(int orderId) {
		// TODO Auto-generated method stub
		Optional<Order> op = orderRepository.findById(orderId);

		if (!op.isEmpty()) {
			orderRepository.delete(op.get());
			return new ResponseEntity<>("Successfully Deleted", HttpStatusCode.valueOf(200));
		} else {
			throw new OrderNotFoundException("Order not found with ID " + orderId);
		}
	}

}

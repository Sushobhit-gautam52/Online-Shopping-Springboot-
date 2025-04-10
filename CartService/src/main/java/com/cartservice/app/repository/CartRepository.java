package com.cartservice.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cartservice.app.entity.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer>{

	List<Cart> findByCustomerId(int customerId);


}

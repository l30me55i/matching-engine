package com.baraka.matching_engine.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baraka.matching_engine.dto.Order;
import com.baraka.matching_engine.request.CreateOrderRequest;
import com.baraka.matching_engine.service.OrdersService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController()
@RequestMapping("/orders")
@Log4j2
@AllArgsConstructor
public class OrdersController {

    
    private final OrdersService ordersService;

    @PostMapping()
    public ResponseEntity<Order> createOrder(@Valid() @RequestBody() CreateOrderRequest orderRequest ) {
        log.info("Request received for creating order for asset {} with price {} and amount {}", orderRequest.getAsset(), orderRequest.getPrice(), orderRequest.getAmount());
        Order order = ordersService.createOrder(orderRequest);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderDetails(@PathVariable("id") String id) {
        Order order = ordersService.getOrderDetails(id);
        if (ObjectUtils.isEmpty(order)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(order);
        
    }
    
}

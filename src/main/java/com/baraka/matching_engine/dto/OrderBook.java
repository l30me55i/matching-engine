package com.baraka.matching_engine.dto;

import java.util.PriorityQueue;

import org.springframework.stereotype.Component;

// Creating 2 Priority queues to manage the sequence of Buy and sell orders based on time the order is created and it's price.
@Component
public class OrderBook {
    private PriorityQueue<Order> buyOrders;
    private PriorityQueue<Order> sellOrders;

    // Initializing the order books for buy and sell orders at startup.
    // Since the customer's priority is to buy the asset at the least price and sell the at the highest price, we will use customized comparators to create buy and sell priority queues (PQs).
    public OrderBook() {
        this.buyOrders = new PriorityQueue<>(Order.BUY_COMPARATOR);
        this.sellOrders = new PriorityQueue<>(Order.SELL_COMPARATOR);
    }

    public PriorityQueue<Order> getBuyOrders() {
        return this.buyOrders;
    }

    public PriorityQueue<Order> getSellOrders() {
        return this.sellOrders;
    }
    
}

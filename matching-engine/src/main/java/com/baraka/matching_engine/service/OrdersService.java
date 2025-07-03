package com.baraka.matching_engine.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;
import com.baraka.matching_engine.dto.Order;
import com.baraka.matching_engine.dto.OrderBook;
import com.baraka.matching_engine.enums.Direction;
import com.baraka.matching_engine.request.CreateOrderRequest;
import com.baraka.matching_engine.util.OrderUtil;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@AllArgsConstructor
public class OrdersService {

    private OrderBook orderBook;
    private static Map<BigInteger, Order> allOrders = new ConcurrentHashMap<>();

    public Order createOrder(CreateOrderRequest createOrderRequest) {

        // build the order object from the create order request and save it to local
        // hashmap memory.
        Order incomingOrder = OrderUtil.buildBasicOrderFromRequest(createOrderRequest, allOrders);

        // Order book contains the PriorityQueues(PQ), we need to check first if it's a
        // buy or a sell order
        PriorityQueue<Order> sellOrders = orderBook.getSellOrders();
        PriorityQueue<Order> buyOrders = orderBook.getBuyOrders();

        if (createOrderRequest.getDirection().equals(Direction.BUY)) {
            return fulfillOrder(sellOrders, buyOrders, incomingOrder, Direction.BUY);

        } else {
            return fulfillOrder(buyOrders, sellOrders, incomingOrder, Direction.SELL);
        }
    }

    private Order fulfillOrder(PriorityQueue<Order> fulfillmentQueue, PriorityQueue<Order> updationQueue,
            Order incomingOrder, Direction direction) {

        while (!fulfillmentQueue.isEmpty()) {

            Order existingOrder = fulfillmentQueue.peek();

            // For Buy orders, check to compare the least priced SELL ORDER available in the
            // sell order book.
            // For Sell orders, check to compare the highest priced BUY ORDER available in
            // the buy order book.
            boolean noPriceMatchAvailable = direction.equals(Direction.BUY)
                    ? fulfillmentQueue.peek().getPrice().compareTo(incomingOrder.getPrice()) > 0
                    : fulfillmentQueue.peek().getPrice().compareTo(incomingOrder.getPrice()) < 0;
            if (noPriceMatchAvailable) {
                break;
            }
            // method to update the trades for incoming and existing orders, along with
            // their respective pending amounts.
            OrderUtil.updateTradesForOrder(existingOrder, incomingOrder, allOrders);

            // removing element from the order book if it has no further use.
            if (existingOrder.getPendingAmount().compareTo(BigDecimal.ZERO) <= 0) {
                fulfillmentQueue.poll();
            }
            // condition to check if the incoming order has been completely satisfied or
            // not.
            if (incomingOrder.getPendingAmount().compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }
        }
        // in case when the request comes and no available order matched or queues are
        // empty
        if (incomingOrder.getPendingAmount().compareTo(BigDecimal.ZERO) > 0) {
            updationQueue.add(incomingOrder);
        }
        return incomingOrder;
    }

    // get order details api implementation.
    public Order getOrderDetails(String id) {
        return allOrders.get(BigInteger.valueOf(Long.valueOf(id)));
    }
}

package com.baraka.matching_engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.PriorityQueue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.baraka.matching_engine.dto.Order;
import com.baraka.matching_engine.dto.OrderBook;
import com.baraka.matching_engine.enums.Direction;
import com.baraka.matching_engine.request.CreateOrderRequest;
import com.baraka.matching_engine.service.OrdersService;

class OrderServiceTests {

    public OrdersService ordersService;
    public OrderBook orderBook;
    private CreateOrderRequest createOrderRequest;

    @BeforeEach
    void init() {
        orderBook = Mockito.mock(OrderBook.class);
        ordersService = new OrdersService(orderBook);
        createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setAmount(BigDecimal.valueOf(12));
        createOrderRequest.setAsset("BTC");
        createOrderRequest.setDirection(Direction.SELL);
        createOrderRequest.setPrice(BigDecimal.valueOf(1000));
        when(orderBook.getBuyOrders()).thenReturn(new PriorityQueue<Order>(Order.BUY_COMPARATOR));
        when(orderBook.getSellOrders()).thenReturn(new PriorityQueue<Order>(Order.SELL_COMPARATOR));
    }

    // when fulfillment queue is empty.
    @Test
    void fulfillmentQueueEmpty() {
        Order order = ordersService.createOrder(createOrderRequest);
        assertTrue(order.getTrades().isEmpty());
    }

    // when sell queue does not have a matching amount element for an incoming buy
    // order
    @Test
    void noMatchingElementInSellQueue() {
        createOrderRequest.setDirection(Direction.BUY);

        Order notMatchingSellorder = Order.builder().price(BigDecimal.valueOf(2000)).direction(Direction.SELL)
                .amount(BigDecimal.valueOf(20)).build();
        PriorityQueue<Order> sellOrderQueue = new PriorityQueue<>(Order.SELL_COMPARATOR);
        sellOrderQueue.add(notMatchingSellorder);
        when(orderBook.getSellOrders()).thenReturn(sellOrderQueue);

        Order order = ordersService.createOrder(createOrderRequest);
        assertTrue(order.getTrades().isEmpty());
    }

    // when buy queue does not have a matching amount element for an incoming sell
    // order
    @Test
    void noMatchingElementInBuyQueue() {
        Order notMatchingBuyorder = Order.builder().price(BigDecimal.valueOf(200)).direction(Direction.BUY)
                .amount(BigDecimal.valueOf(20)).build();
        PriorityQueue<Order> buyOrderQueue = new PriorityQueue<>(Order.BUY_COMPARATOR);
        buyOrderQueue.add(notMatchingBuyorder);
        when(orderBook.getBuyOrders()).thenReturn(buyOrderQueue);

        Order order = ordersService.createOrder(createOrderRequest);
        assertTrue(order.getTrades().isEmpty());
    }

    // when buy queue has a matching element for incoming sell order when equal or
    // more amount
    @Test
    void matchingElementInBuyQueue() {
        Order matchingBuyorder = Order.builder().price(BigDecimal.valueOf(2000)).direction(Direction.BUY)
                .amount(BigDecimal.valueOf(20)).pendingAmount(BigDecimal.valueOf(20)).trades(new ArrayList<>())
                .id(BigInteger.TEN).build();
        PriorityQueue<Order> buyOrderQueue = new PriorityQueue<>(Order.BUY_COMPARATOR);
        buyOrderQueue.add(matchingBuyorder);
        when(orderBook.getBuyOrders()).thenReturn(buyOrderQueue);

        Order order = ordersService.createOrder(createOrderRequest);
        assertTrue(!order.getTrades().isEmpty());
        assertEquals(order.getTrades().size(), 1);
        assertEquals(BigDecimal.ZERO,order.getPendingAmount());
    }

    // when buy queue has a matching element for incoming sell order queue but
    // enough amount is not available to satisfy the order
    @Test
    void matchingElementInBuyQueueButAmountNotEnough() {
        createOrderRequest.setAmount(BigDecimal.valueOf(200));
        Order matchingBuyorder = Order.builder().price(BigDecimal.valueOf(2000)).direction(Direction.BUY)
                .amount(BigDecimal.valueOf(20)).pendingAmount(BigDecimal.valueOf(20)).trades(new ArrayList<>())
                .id(BigInteger.TEN).build();
        PriorityQueue<Order> buyOrderQueue = new PriorityQueue<>(Order.BUY_COMPARATOR);
        buyOrderQueue.add(matchingBuyorder);
        when(orderBook.getBuyOrders()).thenReturn(buyOrderQueue);

        Order order = ordersService.createOrder(createOrderRequest);
        assertTrue(!order.getTrades().isEmpty());
        assertEquals(order.getTrades().size(), 1);
        assertTrue(order.getPendingAmount().compareTo(BigDecimal.ZERO) > 0);
    }

    // when sell queue has a matching element for incoming buy order when equal or
    // more amount
    @Test
    void matchingElementInSellQueue() {
        createOrderRequest.setDirection(Direction.BUY);

        Order matchingSellorder = Order.builder().price(BigDecimal.valueOf(200)).direction(Direction.SELL)
                .amount(BigDecimal.valueOf(20)).pendingAmount(BigDecimal.valueOf(20)).trades(new ArrayList<>())
                .id(BigInteger.TEN).build();
        PriorityQueue<Order> sellOrderQueue = new PriorityQueue<>(Order.SELL_COMPARATOR);
        sellOrderQueue.add(matchingSellorder);
        when(orderBook.getSellOrders()).thenReturn(sellOrderQueue);

        Order order = ordersService.createOrder(createOrderRequest);
        assertTrue(!order.getTrades().isEmpty());
        assertEquals(order.getTrades().size(), 1);
        assertTrue(order.getPendingAmount().compareTo(BigDecimal.ZERO) == 0);
    }

    // when sell queue has a matching element for incoming buy order queue but
    // enough amount is not available to satisfy the order
    @Test
    void matchingElementInSellQueueButAmountNotEnough() {
        createOrderRequest.setDirection(Direction.BUY);
        createOrderRequest.setAmount(BigDecimal.valueOf(200));

        Order matchingSellorder = Order.builder().price(BigDecimal.valueOf(200)).direction(Direction.SELL)
                .amount(BigDecimal.valueOf(20)).pendingAmount(BigDecimal.valueOf(20)).trades(new ArrayList<>())
                .id(BigInteger.TEN).build();
        PriorityQueue<Order> sellOrderQueue = new PriorityQueue<>(Order.SELL_COMPARATOR);
        sellOrderQueue.add(matchingSellorder);
        when(orderBook.getSellOrders()).thenReturn(sellOrderQueue);

        Order order = ordersService.createOrder(createOrderRequest);
        assertTrue(!order.getTrades().isEmpty());
        assertEquals(order.getTrades().size(), 1);
        assertTrue(order.getPendingAmount().compareTo(BigDecimal.ZERO) > 0);
    }

}

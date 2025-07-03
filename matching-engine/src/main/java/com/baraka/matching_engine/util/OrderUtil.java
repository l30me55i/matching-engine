package com.baraka.matching_engine.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Component;
import com.baraka.matching_engine.dto.Order;
import com.baraka.matching_engine.dto.Trade;
import com.baraka.matching_engine.request.CreateOrderRequest;

@Component
public class OrderUtil {

    private OrderUtil() {
    }

    private static final AtomicInteger counter = new AtomicInteger(0);

    public static Order buildBasicOrderFromRequest(CreateOrderRequest createOrderRequest,
            Map<BigInteger, Order> allOrders) {

        Order inputOrder = Order.builder()
                .id(BigInteger.valueOf(counter.getAndIncrement()))
                .timestamp(String.valueOf(ZonedDateTime.now()))
                .asset(createOrderRequest.getAsset())
                .amount(createOrderRequest.getAmount())
                .price(createOrderRequest.getPrice())
                .direction(createOrderRequest.getDirection())
                .trades(new ArrayList<>())
                .pendingAmount(createOrderRequest.getAmount())
                .build();

        // the incoming order needs to also be saved to some local memory.
        allOrders.put(inputOrder.getId(), inputOrder);

        return inputOrder;
    }

    public static void updateTradesForOrder(Order existingOrder, Order incomingOrder,
            Map<BigInteger, Order> allOrders) {
        List<Trade> existingOrderTrades = existingOrder.getTrades();
        List<Trade> incomingOrderTrades = incomingOrder.getTrades();
        
        BigDecimal minAmount = existingOrder.getPendingAmount().min(incomingOrder.getAmount());
        
        Trade incomingOrderTrade = Trade.builder().amount(minAmount).orderId(incomingOrder.getId())
                .price(incomingOrder.getPrice()).build();
        Trade existingOrderTrade = Trade.builder().amount(minAmount).orderId(existingOrder.getId())
                .price(existingOrder.getPrice()).build();

        existingOrderTrades.add(incomingOrderTrade);
        incomingOrderTrades.add(existingOrderTrade);

        existingOrder.setTrades(existingOrderTrades);
        incomingOrder.setTrades(incomingOrderTrades);

        existingOrder.setPendingAmount(
                existingOrder.getPendingAmount().subtract(minAmount).compareTo(BigDecimal.ZERO) < 0
                        ? BigDecimal.ZERO
                        : existingOrder.getPendingAmount().subtract(minAmount));
                        
        incomingOrder.setPendingAmount(
            incomingOrder.getPendingAmount().subtract(minAmount).compareTo(BigDecimal.ZERO) < 0
                        ? BigDecimal.ZERO
                        : incomingOrder.getPendingAmount().subtract(minAmount));

        allOrders.computeIfPresent(existingOrder.getId(), (k, v) -> existingOrder);
        allOrders.computeIfPresent(incomingOrder.getId(), (k, v) -> incomingOrder);
    }

}

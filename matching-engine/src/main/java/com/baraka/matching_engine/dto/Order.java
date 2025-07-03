package com.baraka.matching_engine.dto;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import com.baraka.matching_engine.enums.Direction;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Order {
    private BigInteger id;
    private String timestamp;
    private String asset;
    private BigDecimal price;
    private BigDecimal amount;
    private Direction direction;
    private BigDecimal pendingAmount;
    private List<Trade> trades;

    public static final Comparator<Order> SELL_COMPARATOR = Comparator
            .comparing((Order o) -> o.price)
            .thenComparing(o -> o.timestamp);

    // reversedOrder() added to make sure that the highest priced BUY Order stays at
    // the top of the PQ.
    public static final Comparator<Order> BUY_COMPARATOR = Comparator
            .comparing((Order o) -> o.price, Comparator.reverseOrder())
            .thenComparing(o -> o.timestamp);

}
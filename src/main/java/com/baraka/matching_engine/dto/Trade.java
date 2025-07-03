package com.baraka.matching_engine.dto;

import java.math.BigDecimal;
import java.math.BigInteger;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class Trade {
    private BigInteger orderId;
    private BigDecimal amount;
    private BigDecimal price;
}

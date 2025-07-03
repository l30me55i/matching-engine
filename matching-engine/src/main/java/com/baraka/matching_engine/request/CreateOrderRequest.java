package com.baraka.matching_engine.request;

import java.math.BigDecimal;
import com.baraka.matching_engine.enums.Direction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {

    private String asset;
    private BigDecimal price;
    private BigDecimal amount;
    private Direction direction;
}

package com.baraka.matching_engine.request;

import java.math.BigDecimal;
import com.baraka.matching_engine.enums.Direction;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    @NotNull
    private String asset;
    @Positive
    @NotNull
    private BigDecimal price;
    @Positive
    @NotNull
    private BigDecimal amount;
    private Direction direction;
}

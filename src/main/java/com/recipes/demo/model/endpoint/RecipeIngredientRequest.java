package com.recipes.demo.model.endpoint;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
public class RecipeIngredientRequest {

    @NotNull
    Long ingredientId;
    @Positive(message = "Quantity must not be zero.")
    float quantity;
    @NotNull(message = "Measurement unit must not be empty.")
    Long measurementUnitId;
}

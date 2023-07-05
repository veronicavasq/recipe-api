package com.recipes.demo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IngredientRecipeDTO {

    @NotNull
    private Long ingredientId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String ingredientName;
    @Positive(message = "Quantity must not be zero.")
    private float quantity;
    @NotNull(message = "Measurement unit must not be empty.")
    private Long measurementUnitId;
}

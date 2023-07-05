package com.recipes.demo.model.endpoint;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

import static com.recipes.demo.util.RecipeUtil.MINIMUM_INGREDIENT_NUMBER;

@Value
@NoArgsConstructor(force = true)
public class RecipeRequest {

    @NotBlank(message = "Name must not be null.")
    String name;
    @NotBlank(message = "Instructions must not be null.")
    @Size(min = 1, max = 500, message = "Instructions must be maximum 500 characters.")
    String instructions;
    @Positive
    int servingsNumber;
    @NotNull(message = "Ingredient list must not be null.")
    @Size(min = MINIMUM_INGREDIENT_NUMBER, message = "Recipe must has at least {} ingredient.")
    List<RecipeIngredientRequest> ingredients;
}

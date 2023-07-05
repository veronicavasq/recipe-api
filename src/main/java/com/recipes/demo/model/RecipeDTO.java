package com.recipes.demo.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

import static com.recipes.demo.util.RecipeUtil.MINIMUM_INGREDIENT_NUMBER;

@Data
@Builder
public class RecipeDTO {
    private Long id;
    @NotBlank(message = "Name must not be null.")
    private String name;
    @NotBlank(message = "Instructions must not be null.")
    @Size(min = 1, max = 500, message = "Instructions must be maximum 500 characters.")
    private String instructions;
    @Positive
    private int servingsNumber;
    private RecipeType recipeType;
    private LocalDateTime creationDate;
    @NotNull(message = "Ingredient list must not be null.")
    @Size(min = MINIMUM_INGREDIENT_NUMBER, message = "Recipe must has at least {} ingredient.")
    private List<IngredientRecipeDTO> ingredients;
    private LocalDateTime updatedDate;

}

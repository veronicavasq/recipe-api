package com.recipes.demo.util;

import com.recipes.demo.model.IngredientRecipeDTO;
import com.recipes.demo.model.RecipeDTO;
import com.recipes.demo.model.RecipeType;
import com.recipes.demo.model.endpoint.RecipeRequest;
import com.recipes.demo.repository.entity.Recipe;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public final class RecipeUtil {

    public static final String INGREDIENT_NOT_FOUND = "Not found some of these %s ingredients";
    public static final String MEASUREMENT_UNIT_NOT_FOUND = "Not found some of these %s measurements";
    public static final String RECIPE_NOT_FOUND = "Recipe %s - not found";
    public static final String RECIPE_INVALID_INPUT = "Invalid recipe input fields";
    public static final String RECIPE_WITHOUT_INGREDIENTS = "Recipe should have at least one ingredient";
    public static final int MINIMUM_INGREDIENT_NUMBER = 1;
    public static final String MAXIMUM_NUMBER_INGREDIENTS_FILTER_MESSAGE = "The maximum number of ingredients to filter is %s";
    public static final Long VEGETARIAN_DEFAULT_ID = 1L;
    public static final int MAXIMUM_NUMBER_INGREDIENTS_FILTER = 10;
    public static final String RECIPE_INGREDIENT_INVALID_INPUT = "Quantity and measurement unit is required";

    public static RecipeDTO getBuildRecipeDTO(Recipe recipe, List<IngredientRecipeDTO> recipeIngredientList) {
        return RecipeDTO.builder()
                .recipeType(RecipeType.fromString(recipe.getRecipeType()))
                .creationDate(recipe.getCreationDate())
                .id(recipe.getId())
                .name(recipe.getName())
                .servingsNumber(recipe.getServingsNumber())
                .instructions(recipe.getInstructions())
                .ingredients(recipeIngredientList)
                .build();
    }

    public static RecipeDTO getRecipeDTO(RecipeRequest recipeRequest) {
        List<IngredientRecipeDTO> ingredients = CollectionUtils.emptyIfNull(recipeRequest.getIngredients())
                .stream()
                .map(ingredient -> IngredientRecipeDTO.builder()
                        .ingredientId(ingredient.getIngredientId())
                        .quantity(ingredient.getQuantity())
                        .measurementUnitId(ingredient.getMeasurementUnitId())
                        .build())
                .toList();

        return RecipeDTO.builder()
                .name(recipeRequest.getName())
                .instructions(recipeRequest.getInstructions())
                .servingsNumber(recipeRequest.getServingsNumber())
                .ingredients(ingredients)
                .build();
    }

}

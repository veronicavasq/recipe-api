package com.recipes.demo.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.recipes.demo.model.RecipeDTO;
import com.recipes.demo.model.RecipeType;
import com.recipes.demo.model.endpoint.RecipeRequest;
import com.recipes.demo.repository.entity.Ingredient;
import com.recipes.demo.repository.entity.IngredientRecipe;
import com.recipes.demo.repository.entity.MeasurementUnit;
import com.recipes.demo.repository.entity.Recipe;

import java.time.LocalDateTime;
import java.util.List;

public class TestUtil {
    public static final String RECIPE_INFO = """ 
                      {
                          "name": "Spaghetti",
                          "instructions": "1.Boil water in a large pot. 2.Salt the water with at least 1 tablespoon, and more is fine. 3.Add pasta. 4.Stir occasionally. 5.Drain.",
                          "servingsNumber": 4,
                          "recipeType": "Vegetarian",
                          "ingredients": [
                           {
                            "ingredientId": 1,
                            "ingredientName": "Pasta",
                            "quantity": 250,
                            "measurementUnitId": 1
                           },
                           {
                            "ingredientId": 2,
                            "ingredientName": "Water",
                            "quantity": 1,
                            "measurementUnitId": 2
                           },
                           {
                            "ingredientId": 3,
                            "ingredientName": "Salt",
                            "quantity": 1,
                            "measurementUnitId": 3
                           }
                           ]
                        }
            """;

    public static final String RECIPE_REQUEST = """ 
                      {
                          "name": "Spaghetti",
                          "instructions": "1.Boil water in a large pot. 2.Salt the water with at least 1 tablespoon, and more is fine. 3.Add pasta. 4.Stir occasionally. 5.Drain.",
                          "servingsNumber": 4,
                          "ingredients": [
                           {
                            "ingredientId": 1,
                            "quantity": 250,
                            "measurementUnitId": 1
                           },
                           {
                            "ingredientId": 2,
                            "quantity": 1,
                            "measurementUnitId": 2
                           },
                           {
                            "ingredientId": 3,
                            "quantity": 1,
                            "measurementUnitId": 3
                           }
                           ]
                        }
            """;

    public static final String RECIPE_UPDATE_REQUEST = """ 
                      {
                          "name": "Spaghetti 1",
                          "instructions": "Enjoy",
                          "servingsNumber": 5,
                          "ingredients": [
                           {
                                "ingredientId": 1,
                                "quantity": 250,
                                "measurementUnitId": 1
                           },
                           {
                                "ingredientId": 3,
                                "quantity": 2,
                                "measurementUnitId": 3
                           }
                           ]
                        }
            """;


    public static ObjectMapper buildObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return mapper;
    }

    public static RecipeDTO getDummyRecipeDTO() throws JsonProcessingException {
        return buildObjectMapper().readValue(RECIPE_INFO, RecipeDTO.class);
    }

    public static RecipeDTO getDummyUpdatedRecipeDTO() throws JsonProcessingException {
        RecipeDTO recipeDTO = buildObjectMapper().readValue(RECIPE_UPDATE_REQUEST, RecipeDTO.class);
        recipeDTO.setRecipeType(RecipeType.VEGETARIAN);
        return recipeDTO;
    }

    public static RecipeRequest getDummyRecipeRequest() throws JsonProcessingException {
        return buildObjectMapper().readValue(RECIPE_REQUEST, RecipeRequest.class);
    }

    public static Recipe getDummyRecipeEntity() {
        return Recipe.builder()
                .id(1L)
                .name("Spaghetti")
                .recipeType("Vegetarian")
                .servingsNumber(4)
                .creationDate(LocalDateTime.now())
                .instructions("1.Boil water in a large pot. 2.Salt the water with at least 1 tablespoon, and more is fine. 3.Add pasta. 4.Stir occasionally. 5.Drain.")
                .build();
    }

    public static List<Ingredient> getDummyIngredientList() {
        Ingredient ingredient1 = Ingredient.builder()
                .id(1L)
                .name("Pasta")
                .ingredientTypeId(RecipeUtil.VEGETARIAN_DEFAULT_ID)
                .build();

        Ingredient ingredient2 = Ingredient.builder()
                .id(2L)
                .name("Water")
                .ingredientTypeId(RecipeUtil.VEGETARIAN_DEFAULT_ID)
                .build();

        Ingredient ingredient3 = Ingredient.builder()
                .id(3L)
                .name("Salt")
                .ingredientTypeId(RecipeUtil.VEGETARIAN_DEFAULT_ID)
                .build();

        return List.of(ingredient1, ingredient2, ingredient3);
    }

    public static List<MeasurementUnit> getDummyMeasumentUnitList() {
        MeasurementUnit measurementUnit1 = MeasurementUnit.builder()
                .id(1L)
                .name("Gram")
                .build();

        MeasurementUnit measurementUnit2 = MeasurementUnit.builder()
                .id(2L)
                .name("Liter")
                .build();

        MeasurementUnit measurementUnit3 = MeasurementUnit.builder()
                .id(3L)
                .name("Tea Spoon")
                .build();

        return List.of(measurementUnit1, measurementUnit2, measurementUnit3);
    }

    public static List<IngredientRecipe> getDummyRecipeIngredientRecipeList() {
        IngredientRecipe ingredientRecipe1 = IngredientRecipe.builder()
                .id(1L)
                .ingredientId(1L)
                .measurementUnitId(1L)
                .build();

        IngredientRecipe ingredientRecipe2 = IngredientRecipe.builder()
                .id(2L)
                .ingredientId(2L)
                .measurementUnitId(2L)
                .build();

        IngredientRecipe ingredientRecipe3 = IngredientRecipe.builder()
                .id(3L)
                .ingredientId(3L)
                .measurementUnitId(3L)
                .build();

        return List.of(ingredientRecipe1, ingredientRecipe2, ingredientRecipe3);
    }

    public static String asJsonString(final Object obj) {
        try {
            return buildObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}

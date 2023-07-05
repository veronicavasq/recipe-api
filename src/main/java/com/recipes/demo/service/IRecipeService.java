package com.recipes.demo.service;

import com.recipes.demo.exception.RecipeException;
import com.recipes.demo.exception.RecipeNotFoundException;
import com.recipes.demo.model.PaginatedRecipeResponse;
import com.recipes.demo.model.RecipeDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IRecipeService {
    @Transactional
    RecipeDTO saveRecipe(RecipeDTO recipeDTO) throws RecipeException;

    @Transactional
    RecipeDTO updateRecipe(Long recipeId, RecipeDTO recipeDTO) throws RecipeException;

    @Transactional
    void deleteRecipe(Long recipeId) throws RecipeNotFoundException;

    PaginatedRecipeResponse getRecipes(Pageable pageable,
                                       Boolean isVegetarian,
                                       List<String> includedIngredients,
                                       List<String> excludedIngredients,
                                       String instructionKeyword,
                                       Integer servings) throws RecipeException;

    RecipeDTO getRecipeById(Long recipeId) throws RecipeNotFoundException;
}

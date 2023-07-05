package com.recipes.demo.repository;

import com.recipes.demo.repository.entity.IngredientRecipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngredientRecipeRepository extends JpaRepository<IngredientRecipe, Long> {
    void deleteByRecipeId(Long productId);

    List<IngredientRecipe> findByRecipeId(Long recipeId);
}

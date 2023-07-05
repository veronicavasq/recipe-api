package com.recipes.demo.repository;

import com.recipes.demo.repository.entity.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@SuppressWarnings("SqlDialectInspection")
@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    @Query(value = """
            SELECT DISTINCT rp.id as id, rp.name, 
            rp.instructions, 
            rp.servings_number, 
            rp.recipe_type,
            rp.creation_date,
            rp.updated_date
            FROM recipe as rp
            INNER JOIN ingredient_recipe as ingxrp on rp.id=ingxrp.recipe_id
            INNER JOIN ingredient as ing on ing.id =ingxrp.ingredient_id
            WHERE (:recipeType is null OR rp.recipe_type = :recipeType)
            AND ( CASE 
            WHEN :filterByIncludedIngredients=1
            THEN UPPER(ing.name) IN :includedIngredients ELSE TRUE END)
            AND  ( CASE 
            WHEN :filterByExcludedIngredients=1
            THEN rp.id NOT IN (SELECT ingxrp1.recipe_id from ingredient_recipe as ingxrp1 INNER JOIN ingredient as ing1\s
                 on ing1.id =ingxrp1.ingredient_id WHERE UPPER(ing1.name) IN :excludedIngredients)
            ELSE TRUE END)
            AND (:instructionKeyWord is null OR rp.instructions like :instructionKeyWord)
            AND (:servings is null OR rp.servings_number=:servings)
                    """, nativeQuery = true)
    Page<Recipe> findAll(Pageable pageable,
                         String recipeType,
                         Boolean filterByIncludedIngredients,
                         Boolean filterByExcludedIngredients,
                         List<String> includedIngredients,
                         List<String> excludedIngredients,
                         String instructionKeyWord,
                         Integer servings);

}

package com.recipes.demo.exception;

public class IngredientNotFoundException extends RecipeException {

    public IngredientNotFoundException(Object additionalData) {
        super(additionalData);
    }


}

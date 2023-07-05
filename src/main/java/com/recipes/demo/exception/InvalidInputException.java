package com.recipes.demo.exception;

public class InvalidInputException extends RecipeException {

    public InvalidInputException(Object additionalData) {
        super(additionalData);
    }
}

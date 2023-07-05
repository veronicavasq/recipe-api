package com.recipes.demo.exception;

public class RecipeException extends Exception {

    private final Object additionalData;

    public RecipeException(Object additionalData) {
        this.additionalData = additionalData;
    }

    public RecipeException(Exception exception, Object additionalData) {
        super(exception.getMessage());
        this.initCause(exception);
        this.additionalData = additionalData;
    }

    public Object getAdditionalData() {
        return this.additionalData;
    }
}

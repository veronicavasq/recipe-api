package com.recipes.demo.exception;

public enum ErrorCode {

    RECIPE_NOT_FOUND_CODE("001"),
    INGREDIENT_NOT_FOUND_CODE("002"),
    MEASUREMENT_UNIT_NOT_FOUND_CODE("003"),
    RECIPE_BAD_REQUEST("004");

    private final String code;

    ErrorCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}

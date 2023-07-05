package com.recipes.demo.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RecipeType {
    VEGETARIAN("Vegetarian"),
    NON_VEGETARIAN("No Vegetarian");

    private final String label;

    RecipeType(String label) {
        this.label = label;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static RecipeType fromString(String label) {
        for (RecipeType e : values()) {
            if (e.label.equalsIgnoreCase(label)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Invalid RecipeType");
    }

    @JsonValue
    public String getLabel() {
        return label;
    }
}

package com.recipes.demo.model;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MeasurementUnitDTO {
    @NotNull
    private Long id;
    private String name;
}

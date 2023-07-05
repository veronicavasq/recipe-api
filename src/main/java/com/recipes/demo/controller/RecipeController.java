package com.recipes.demo.controller;

import com.recipes.demo.exception.ErrorInfo;
import com.recipes.demo.exception.RecipeException;
import com.recipes.demo.model.PaginatedRecipeResponse;
import com.recipes.demo.model.RecipeDTO;
import com.recipes.demo.model.endpoint.RecipeRequest;
import com.recipes.demo.service.IRecipeService;
import com.recipes.demo.util.RecipeUtil;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.recipes.demo.util.RecipeUtil.MAXIMUM_NUMBER_INGREDIENTS_FILTER;

@OpenAPIDefinition(info = @Info(title = "Recipe API", version = "1.0"))
@Tag(name = "Recipe", description = "Recipe operations")
@RestController
@RequestMapping("/recipe")
public class RecipeController {

    private final IRecipeService recipeService;

    public RecipeController(IRecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @Operation(summary = "Get recipe list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stored a recipe",
                    content = {@Content(schema = @Schema(implementation = RecipeDTO.class))}
            ),
            @ApiResponse(responseCode = "400", description = "Invalid recipe",
                    content = {@Content(schema = @Schema(implementation = ErrorInfo.class))}
            ),
            @ApiResponse(responseCode = "404", description = "Some ingredients or measurements were not found",
                    content = {@Content(schema = @Schema(implementation = ErrorInfo.class))}
            )
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public PaginatedRecipeResponse getRecipes(@PageableDefault(size = 20)
                                              @SortDefault.SortDefaults({
                                                      @SortDefault(sort = "name", direction = Sort.Direction.DESC),
                                                      @SortDefault(sort = "id", direction = Sort.Direction.ASC)
                                              }) Pageable pageable,
                                              @Parameter(
                                                      description = "Filter recipes by vegetarian or not vegetarian",
                                                      allowEmptyValue = true
                                              )
                                              @Nullable @RequestParam Boolean isVegetarian,
                                              @Parameter(
                                                      description = "Filter recipes which contain the ingredient name list"
                                              )
                                              @Nullable @Size(max = MAXIMUM_NUMBER_INGREDIENTS_FILTER,
                                                      message = "Maximum 10 ingredients")
                                              @RequestParam List<String> includedIngredients,
                                              @Parameter(
                                                      description = "Filter recipes which not have the ingredient name list"
                                              )
                                              @Nullable @Size(max = MAXIMUM_NUMBER_INGREDIENTS_FILTER,
                                                      message = "Maximum 10 ingredients")
                                              @RequestParam List<String> excludedIngredients,

                                              @Parameter(
                                                      description = "Filter recipes which have the text in the instructions"
                                              )
                                              @Nullable @RequestParam String instructionKeyword,
                                              @Parameter(
                                                      description = "Filter recipes which have servings number"
                                              )
                                              @Nullable @RequestParam Integer servings
    ) throws RecipeException {
        return this.recipeService.getRecipes(pageable, isVegetarian, includedIngredients, excludedIngredients, instructionKeyword,
                servings);
    }


    @Operation(summary = "Save a recipe")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Stored a recipe",
                    content = {@Content(schema = @Schema(implementation = RecipeDTO.class))}
            ),
            @ApiResponse(responseCode = "400", description = "Invalid recipe",
                    content = {@Content(schema = @Schema(implementation = ErrorInfo.class))}
            ),
            @ApiResponse(responseCode = "404", description = "Some ingredients or measurements were not found",
                    content = {@Content(schema = @Schema(implementation = ErrorInfo.class))}
            )
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public RecipeDTO save(@RequestBody RecipeRequest recipeRequest) throws RecipeException {
        return this.recipeService.saveRecipe(RecipeUtil.getRecipeDTO(recipeRequest));
    }

    @Operation(summary = "Update a recipe")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated recipe",
                    content = {@Content(schema = @Schema(implementation = RecipeDTO.class))}
            ),
            @ApiResponse(responseCode = "400", description = "Invalid recipe",
                    content = {@Content(schema = @Schema(implementation = ErrorInfo.class))}
            ),
            @ApiResponse(responseCode = "404", description = "Recipe, ingredients or measurements were not found",
                    content = {@Content(schema = @Schema(implementation = ErrorInfo.class))}
            )
    })
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public RecipeDTO update(@Parameter(description = "Recipe Id")
                            @PathVariable("id") final Long recipeId, @RequestBody RecipeRequest recipeRequest) throws RecipeException {
        return this.recipeService.updateRecipe(recipeId, RecipeUtil.getRecipeDTO(recipeRequest));
    }

    @Operation(summary = "Delete a recipe")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deleted recipe"),
            @ApiResponse(responseCode = "404", description = "Recipe were not found",
                    content = {@Content(schema = @Schema(implementation = ErrorInfo.class))}
            )
    })
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@Parameter(description = "Recipe Id")
                       @PathVariable("id") final Long recipeId) throws RecipeException {
        this.recipeService.deleteRecipe(recipeId);
    }

    @Operation(summary = "Get a recipe")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get recipe successfully"),
            @ApiResponse(responseCode = "404", description = "Recipe were not found",
                    content = {@Content(schema = @Schema(implementation = ErrorInfo.class))}
            )
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public RecipeDTO getRecipeById(@Parameter(description = "Recipe Id")
                                   @PathVariable("id") final Long recipeId) throws RecipeException {
        return this.recipeService.getRecipeById(recipeId);
    }

}

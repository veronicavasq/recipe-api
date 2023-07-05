package com.recipes.demo.service.impl;

import com.recipes.demo.exception.*;
import com.recipes.demo.model.IngredientRecipeDTO;
import com.recipes.demo.model.PaginatedRecipeResponse;
import com.recipes.demo.model.RecipeDTO;
import com.recipes.demo.model.RecipeType;
import com.recipes.demo.repository.IngredientRecipeRepository;
import com.recipes.demo.repository.IngredientRepository;
import com.recipes.demo.repository.MeasurementUnitRepository;
import com.recipes.demo.repository.RecipeRepository;
import com.recipes.demo.repository.entity.Ingredient;
import com.recipes.demo.repository.entity.IngredientRecipe;
import com.recipes.demo.repository.entity.MeasurementUnit;
import com.recipes.demo.repository.entity.Recipe;
import com.recipes.demo.service.IRecipeService;
import com.recipes.demo.util.RecipeUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.recipes.demo.util.RecipeUtil.*;

@Service
public class RecipeService implements IRecipeService {

    private final RecipeRepository recipeRepository;
    private final IngredientRecipeRepository ingredientRecipeRepository;
    private final IngredientRepository ingredientRepository;
    private final MeasurementUnitRepository measurementUnitRepository;

    public RecipeService(RecipeRepository recipeRepository, IngredientRecipeRepository ingredientRecipeRepository, IngredientRepository ingredientRepository, MeasurementUnitRepository measurementUnitRepository) {
        this.recipeRepository = recipeRepository;
        this.ingredientRecipeRepository = ingredientRecipeRepository;
        this.ingredientRepository = ingredientRepository;
        this.measurementUnitRepository = measurementUnitRepository;
    }

    private static void validateIngredientMinimumNumber(RecipeDTO recipeDTO) throws InvalidInputException {
        if (recipeDTO.getIngredients().size() < MINIMUM_INGREDIENT_NUMBER) {
            throw new InvalidInputException(new ErrorInfo(ErrorCode.RECIPE_BAD_REQUEST.getCode(), RECIPE_WITHOUT_INGREDIENTS));
        }
    }

    private static void validateIngredientRecipe(IngredientRecipeDTO ingredientRecipeDTO) throws InvalidInputException {
        if (ingredientRecipeDTO.getQuantity() <= 0) {
            throw new InvalidInputException(new ErrorInfo(ErrorCode.RECIPE_BAD_REQUEST.getCode(), RECIPE_INGREDIENT_INVALID_INPUT));
        }
    }

    @Override
    @Transactional
    public RecipeDTO saveRecipe(RecipeDTO recipeDTO) throws RecipeException {
        validateRecipeInput(recipeDTO);
        List<Ingredient> validIngredients = getExistingIngredient(recipeDTO);
        validateMeasurementUnitsByRecipe(recipeDTO);

        RecipeType recipeType = getRecipeTypeByIngredients(validIngredients);
        Recipe recipeToSave = Recipe.builder()
                .name(recipeDTO.getName())
                .servingsNumber(recipeDTO.getServingsNumber())
                .instructions(recipeDTO.getInstructions())
                .recipeType(recipeType.getLabel())
                .creationDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        Recipe recipe = recipeRepository.save(recipeToSave);
        ingredientRecipeRepository.deleteByRecipeId(recipe.getId());
        this.ingredientRecipeRepository.saveAll(this.buildRecipeIngredientsEntity(recipeDTO, recipe));
        recipeDTO.setId(recipe.getId());
        recipeDTO.setRecipeType(RecipeType.fromString(recipe.getRecipeType()));
        recipeDTO.setCreationDate(recipe.getCreationDate());
        recipeDTO.setUpdatedDate(recipe.getUpdatedDate());
        return recipeDTO;
    }

    private RecipeType getRecipeTypeByIngredients(List<Ingredient> validIngredients) {
        List<Ingredient> noVegetarianIngredients = validIngredients.stream()
                .filter(ing -> !VEGETARIAN_DEFAULT_ID.equals(ing.getIngredientTypeId()))
                .toList();
        return noVegetarianIngredients.isEmpty() ? RecipeType.VEGETARIAN : RecipeType.NON_VEGETARIAN;
    }

    private List<IngredientRecipe> buildRecipeIngredientsEntity(RecipeDTO recipeDTO, Recipe recipe) throws RecipeException {
        List<IngredientRecipe> ingredients = new ArrayList<>();
        for (IngredientRecipeDTO ingredientRecipeDTO : recipeDTO.getIngredients()) {
            validateIngredientRecipe(ingredientRecipeDTO);
            IngredientRecipe ingredientRecipe = IngredientRecipe.builder()
                    .ingredientId(ingredientRecipeDTO.getIngredientId())
                    .recipeId(recipe.getId())
                    .measurementUnitId(ingredientRecipeDTO.getMeasurementUnitId())
                    .quantity(ingredientRecipeDTO.getQuantity())
                    .build();
            ingredients.add(ingredientRecipe);
        }
        return ingredients;
    }

    private List<Ingredient> getExistingIngredient(RecipeDTO recipeDTO) throws IngredientNotFoundException {
        Set<Long> ingredientIds = recipeDTO.getIngredients()
                .stream()
                .map(IngredientRecipeDTO::getIngredientId)
                .collect(Collectors.toSet());

        List<Ingredient> ingredients = ingredientRepository.findByIdIn(ingredientIds);
        if (ingredients.isEmpty() || ingredients.size() != ingredientIds.size()) {
            String errorMessage = String.format(RecipeUtil.INGREDIENT_NOT_FOUND, ingredientIds);
            throw new IngredientNotFoundException(new ErrorInfo(ErrorCode.INGREDIENT_NOT_FOUND_CODE.getCode(), errorMessage));
        }

        return ingredients;
    }

    private void validateMeasurementUnitsByRecipe(RecipeDTO recipeDTO) throws MeasurementUnitNotFoundException {
        Set<Long> measurementIds = recipeDTO.getIngredients().stream().map(IngredientRecipeDTO::getMeasurementUnitId).collect(Collectors.toSet());
        List<MeasurementUnit> measurements = measurementUnitRepository.findByIdIn(measurementIds);

        if (measurements.isEmpty() || measurements.size() != measurementIds.size()) {
            String errorMessage = String.format(RecipeUtil.MEASUREMENT_UNIT_NOT_FOUND, measurementIds);
            throw new MeasurementUnitNotFoundException(new ErrorInfo(ErrorCode.MEASUREMENT_UNIT_NOT_FOUND_CODE.getCode(), errorMessage));
        }
    }

    @Override
    @Transactional
    public RecipeDTO updateRecipe(Long recipeId, RecipeDTO recipeDTO) throws RecipeException {
        validateRecipeInput(recipeDTO);
        getExistingRecipeById(recipeId);

        List<Ingredient> validIngredients = getExistingIngredient(recipeDTO);
        validateMeasurementUnitsByRecipe(recipeDTO);

        RecipeType recipeType = getRecipeTypeByIngredients(validIngredients);
        Recipe recipeToUpdate = Recipe.builder()
                .name(recipeDTO.getName())
                .servingsNumber(recipeDTO.getServingsNumber())
                .instructions(recipeDTO.getInstructions())
                .recipeType(recipeType.getLabel())
                .id(recipeId)
                .updatedDate(LocalDateTime.now())
                .build();

        recipeRepository.save(recipeToUpdate);
        ingredientRecipeRepository.deleteByRecipeId(recipeToUpdate.getId());
        this.ingredientRecipeRepository.saveAll(this.buildRecipeIngredientsEntity(recipeDTO, recipeToUpdate));
        recipeDTO.setId(recipeId);
        recipeDTO.setRecipeType(RecipeType.fromString(recipeToUpdate.getRecipeType()));
        return recipeDTO;
    }

    @Override
    @Transactional
    public void deleteRecipe(Long recipeId) throws RecipeNotFoundException {
        getExistingRecipeById(recipeId);
        ingredientRecipeRepository.deleteByRecipeId(recipeId);
        recipeRepository.deleteById(recipeId);
    }

    private Recipe getExistingRecipeById(Long recipeId) throws RecipeNotFoundException {
        Optional<Recipe> recipeOptional = recipeRepository.findById(recipeId);
        if (recipeOptional.isEmpty()) {
            String errorMessage = String.format(RecipeUtil.RECIPE_NOT_FOUND, recipeId);
            throw new RecipeNotFoundException(new ErrorInfo(ErrorCode.RECIPE_NOT_FOUND_CODE.getCode(), errorMessage));
        }
        return recipeOptional.get();
    }

    @Override
    public PaginatedRecipeResponse getRecipes(Pageable pageable,
                                              Boolean isVegetarian,
                                              List<String> includedIngredients,
                                              List<String> excludedIngredients,
                                              String instructionKeyword,
                                              Integer servings) throws RecipeException {

        String recipeTypeFilter = Optional.ofNullable(isVegetarian)
                .map(veggie -> isVegetarian ? RecipeType.VEGETARIAN.getLabel() : RecipeType.NON_VEGETARIAN.getLabel())
                .orElse(null);
        String instructionKeywordParam = Optional.ofNullable(instructionKeyword)
                .filter(StringUtils::isNotBlank)
                .map(kword -> "%" + kword + "%")
                .orElse(null);
        List<String> includedIngredientsParam = getValidateIngredientFilter(includedIngredients);
        List<String> excludedIngredientsParam = getValidateIngredientFilter(excludedIngredients);
        Page<Recipe> recipes = this.recipeRepository.findAll(pageable,
                recipeTypeFilter,
                !includedIngredientsParam.isEmpty(),
                !excludedIngredientsParam.isEmpty(),
                includedIngredientsParam, excludedIngredientsParam,
                instructionKeywordParam, servings);

        Page<RecipeDTO> page = recipes.map(recipe -> getBuildRecipeDTO(recipe, new ArrayList<>()));
        return new PaginatedRecipeResponse(page);
    }

    private List<String> getValidateIngredientFilter(List<String> ingredientsToFilter) throws InvalidInputException {
        if (ingredientsToFilter != null && MAXIMUM_NUMBER_INGREDIENTS_FILTER < ingredientsToFilter.size()) {
            String errorMessage = String.format(MAXIMUM_NUMBER_INGREDIENTS_FILTER_MESSAGE, MAXIMUM_NUMBER_INGREDIENTS_FILTER);
            throw new InvalidInputException(new ErrorInfo(ErrorCode.RECIPE_BAD_REQUEST.getCode(), errorMessage));
        }

        return CollectionUtils.emptyIfNull(ingredientsToFilter).stream().map(String::toUpperCase).toList();
    }

    @Override
    public RecipeDTO getRecipeById(Long recipeId) throws RecipeNotFoundException {
        Recipe recipe = getExistingRecipeById(recipeId);
        List<IngredientRecipe> recipeIngredients = ingredientRecipeRepository.findByRecipeId(recipeId);
        Set<Long> ingredientsIds = recipeIngredients.stream().map(IngredientRecipe::getIngredientId).collect(Collectors.toSet());
        Map<Long, Ingredient> ingredientEntityMap = ingredientRepository.findByIdIn(ingredientsIds).stream().collect(Collectors.toMap(Ingredient::getId, Function.identity()));

        List<IngredientRecipeDTO> recipeIngredientList = recipeIngredients.stream()
                .map(recipeIng ->
                        IngredientRecipeDTO.builder()
                                .ingredientId(recipeIng.getIngredientId())
                                .ingredientName(ingredientEntityMap.get(recipeIng.getIngredientId()).getName())
                                .measurementUnitId(recipeIng.getMeasurementUnitId())
                                .quantity(recipeIng.getQuantity())
                                .build()
                ).toList();

        return getBuildRecipeDTO(recipe, recipeIngredientList);

    }

    private void validateRecipeInput(RecipeDTO recipeDTO) throws InvalidInputException {
        if (StringUtils.isBlank(recipeDTO.getName())) {
            throw new InvalidInputException(new ErrorInfo(ErrorCode.RECIPE_BAD_REQUEST.getCode(), RECIPE_INVALID_INPUT));
        }
        validateIngredientMinimumNumber(recipeDTO);
    }


}

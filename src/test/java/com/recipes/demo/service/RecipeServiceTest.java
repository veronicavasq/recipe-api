package com.recipes.demo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.recipes.demo.exception.*;
import com.recipes.demo.model.IngredientRecipeDTO;
import com.recipes.demo.model.PaginatedRecipeResponse;
import com.recipes.demo.model.RecipeDTO;
import com.recipes.demo.repository.IngredientRecipeRepository;
import com.recipes.demo.repository.IngredientRepository;
import com.recipes.demo.repository.MeasurementUnitRepository;
import com.recipes.demo.repository.RecipeRepository;
import com.recipes.demo.repository.entity.Recipe;
import com.recipes.demo.service.impl.RecipeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.recipes.demo.util.TestUtil.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class RecipeServiceTest {

    @Mock
    private RecipeRepository recipeRepository;
    @Mock
    private IngredientRecipeRepository ingredientRecipeRepository;
    @Mock
    private IngredientRepository ingredientRepository;
    @Mock
    private MeasurementUnitRepository measurementUnitRepository;
    @InjectMocks
    private RecipeService recipeService;

    @Test
    public void testSaveARecipeSuccessfully() throws JsonProcessingException, RecipeException {
        RecipeDTO recipeDTO = getDummyRecipeDTO();
        Mockito.when(recipeRepository.save(any())).thenReturn(getDummyRecipeEntity());
        Mockito.when(ingredientRepository.findByIdIn(any())).thenReturn(getDummyIngredientList());
        Mockito.when(measurementUnitRepository.findByIdIn(any())).thenReturn(getDummyMeasumentUnitList());
        RecipeDTO savedRecipe = this.recipeService.saveRecipe(recipeDTO);
        Assertions.assertEquals(savedRecipe.getId(), 1L);
        Assertions.assertEquals(savedRecipe.getName(), recipeDTO.getName());
        Assertions.assertNotNull(savedRecipe.getCreationDate());
        Assertions.assertEquals(savedRecipe.getIngredients().size(), recipeDTO.getIngredients().size());
        Mockito.verify(recipeRepository).save(any());
        Mockito.verify(ingredientRecipeRepository).deleteByRecipeId(anyLong());
        Mockito.verify(ingredientRecipeRepository).saveAll(any());
    }

    @Test
    public void testSaveARecipeThrowInvalidIngredientException() throws JsonProcessingException {
        IngredientRecipeDTO ingredientRecipeDTO = IngredientRecipeDTO.builder()
                .ingredientId(100L)
                .build();

        RecipeDTO recipeDTO = getDummyRecipeDTO();
        recipeDTO.getIngredients().add(ingredientRecipeDTO);
        Mockito.when(ingredientRepository.findByIdIn(any())).thenReturn(getDummyIngredientList());
        Assertions.assertThrows(IngredientNotFoundException.class, () -> this.recipeService.saveRecipe(recipeDTO));
    }

    @Test
    public void testSaveARecipeThrowInvalidMeasurementUnitException() throws JsonProcessingException {
        IngredientRecipeDTO ingredientRecipeDTO = IngredientRecipeDTO.builder()
                .ingredientId(1L)
                .measurementUnitId(30L)
                .build();

        RecipeDTO recipeDTO = getDummyRecipeDTO();
        recipeDTO.getIngredients().add(ingredientRecipeDTO);
        Mockito.when(ingredientRepository.findByIdIn(any())).thenReturn(getDummyIngredientList());
        Assertions.assertThrows(MeasurementUnitNotFoundException.class, () -> this.recipeService.saveRecipe(recipeDTO));
    }

    @Test
    public void testSaveARecipeThrowInvalidInputException() throws JsonProcessingException {
        RecipeDTO recipeDTO = getDummyRecipeDTO();
        recipeDTO.setName(null);
        Assertions.assertThrows(InvalidInputException.class, () -> this.recipeService.saveRecipe(recipeDTO));
    }

    @Test
    public void testSaveARecipeWithoutIngredientsThrowInvalidInputException() throws JsonProcessingException {
        RecipeDTO recipeDTO = getDummyRecipeDTO();
        recipeDTO.setIngredients(Collections.emptyList());
        Assertions.assertThrows(InvalidInputException.class, () -> this.recipeService.saveRecipe(recipeDTO));
    }

    @Test
    public void testUpdateRecipeSuccessfully() throws JsonProcessingException, RecipeException {
        Long recipeId = 1L;
        RecipeDTO recipeDTO = getDummyRecipeDTO();
        recipeDTO.setName("Updated name");
        recipeDTO.setId(null);
        Mockito.when(recipeRepository.findById(recipeId)).thenReturn(Optional.ofNullable(getDummyRecipeEntity()));
        Mockito.when(recipeRepository.save(any())).thenReturn(getDummyRecipeEntity());
        Mockito.when(ingredientRepository.findByIdIn(any())).thenReturn(getDummyIngredientList());
        Mockito.when(measurementUnitRepository.findByIdIn(any())).thenReturn(getDummyMeasumentUnitList());
        RecipeDTO updatedRecipe = this.recipeService.updateRecipe(recipeId, recipeDTO);
        Assertions.assertEquals(updatedRecipe.getId(), recipeId);
        Assertions.assertEquals(updatedRecipe.getName(), recipeDTO.getName());
        Mockito.verify(recipeRepository).save(any());
        Mockito.verify(ingredientRecipeRepository).deleteByRecipeId(recipeId);
        Mockito.verify(ingredientRecipeRepository).saveAll(any());
    }

    @Test
    public void testUpdateRecipeThrowNotFoundException() throws JsonProcessingException {
        Long recipeId = 1L;
        RecipeDTO recipeDTO = getDummyRecipeDTO();
        Mockito.when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());
        Assertions.assertThrows(RecipeNotFoundException.class, () -> this.recipeService.updateRecipe(recipeId, recipeDTO));
    }

    @Test
    public void testDeleteRecipeSuccessfully() throws RecipeException {
        Long recipeId = 1L;
        Mockito.when(recipeRepository.findById(recipeId)).thenReturn(Optional.ofNullable(getDummyRecipeEntity()));
        this.recipeService.deleteRecipe(recipeId);
        Mockito.verify(this.recipeRepository, times(1)).deleteById(recipeId);
        Mockito.verify(this.ingredientRecipeRepository, times(1)).deleteByRecipeId(recipeId);
    }

    @Test
    public void testGetRecipeSuccessfully() throws RecipeException {
        Long recipeId = 1L;
        Mockito.when(recipeRepository.findById(recipeId)).thenReturn(Optional.ofNullable(getDummyRecipeEntity()));
        Mockito.when(ingredientRepository.findByIdIn(any())).thenReturn(getDummyIngredientList());
        Mockito.when(ingredientRecipeRepository.findByRecipeId(any())).thenReturn(getDummyRecipeIngredientRecipeList());
        RecipeDTO recipeDTO = this.recipeService.getRecipeById(recipeId);
        Assertions.assertEquals(recipeDTO.getId(), 1L);
        Assertions.assertEquals(recipeDTO.getName(), "Spaghetti");
        Assertions.assertEquals(recipeDTO.getRecipeType().getLabel(), "Vegetarian");
        Assertions.assertFalse(recipeDTO.getInstructions().isBlank());
        Assertions.assertEquals(recipeDTO.getIngredients().size(), 3);
        Assertions.assertEquals(recipeDTO.getIngredients().get(0).getIngredientId(), 1L);
        Assertions.assertEquals(recipeDTO.getIngredients().get(0).getIngredientName(), "Pasta");
        Assertions.assertEquals(recipeDTO.getIngredients().get(0).getMeasurementUnitId(), 1L);
        Assertions.assertEquals(recipeDTO.getIngredients().get(1).getIngredientId(), 2L);
        Assertions.assertEquals(recipeDTO.getIngredients().get(1).getIngredientName(), "Water");
        Assertions.assertEquals(recipeDTO.getIngredients().get(1).getMeasurementUnitId(), 2L);
        Assertions.assertEquals(recipeDTO.getIngredients().get(2).getIngredientId(), 3L);
    }

    @Test
    public void testGetRecipeListSuccessfully() throws RecipeException {
        Pageable pageable = Pageable.ofSize(10);
        Boolean isVegetarian = true;
        List<String> includedIngredients = List.of("pasta", "water");
        List<String> excludedIngredients = List.of("milk");
        String instructionKeyword = "boil";
        Integer servings = 4;
        Page<Recipe> pagedResponse = new PageImpl<>(List.of(getDummyRecipeEntity()),pageable,1);
        Mockito.when(recipeRepository.findAll(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(pagedResponse);
        PaginatedRecipeResponse responsePage = recipeService.getRecipes(pageable, isVegetarian, includedIngredients, excludedIngredients, instructionKeyword, servings);
        Assertions.assertEquals(responsePage.getContent().size(), 1);
    }
}

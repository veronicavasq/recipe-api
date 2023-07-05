package com.recipes.demo.controller;

import com.recipes.demo.config.WebMvcConfig;
import com.recipes.demo.exception.*;
import com.recipes.demo.model.PaginatedRecipeResponse;
import com.recipes.demo.model.RecipeDTO;
import com.recipes.demo.model.RecipeType;
import com.recipes.demo.service.impl.RecipeService;
import com.recipes.demo.util.RecipeUtil;
import com.recipes.demo.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.time.LocalDateTime;
import java.util.List;

import static com.recipes.demo.util.RecipeUtil.RECIPE_INVALID_INPUT;
import static com.recipes.demo.util.TestUtil.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = {RecipeController.class, RecipeExceptionHandler.class, WebMvcConfig.class})
@AutoConfigureMockMvc
@EnableWebMvc
public class RecipeControllerTest {

    @MockBean
    private RecipeService recipeService;
    @Autowired
    private MockMvc mvc;

    @Test
    public void testSaveRecipeSuccessfully() throws Exception {
        RecipeDTO recipeDTO = getDummyRecipeDTO();
        LocalDateTime createdDate = LocalDateTime.parse("2023-07-03T18:49:04.61");
        LocalDateTime updatedDate = LocalDateTime.parse("2023-07-03T20:49:04.61");
        recipeDTO.setCreationDate(createdDate);
        recipeDTO.setUpdatedDate(updatedDate);

        Mockito.when(recipeService.saveRecipe(any())).thenReturn(recipeDTO);

        mvc.perform(post("/recipe")
                        .content(TestUtil.asJsonString(getDummyRecipeRequest()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Spaghetti"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.creationDate").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.updatedDate").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.recipeType").value("Vegetarian"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.servingsNumber").value(4));
    }

    @Test
    public void testSaveRecipeThrowInvalidInputException() throws Exception {
        ErrorInfo errorInfo = (new ErrorInfo(ErrorCode.RECIPE_BAD_REQUEST.getCode(), RECIPE_INVALID_INPUT));
        doThrow(new InvalidInputException(errorInfo)).when(recipeService).saveRecipe(any());

        mvc.perform(post("/recipe")
                        .content(RECIPE_UPDATE_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(ErrorCode.RECIPE_BAD_REQUEST.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.additionalData.message").value("Invalid recipe input fields"));
    }

    @Test
    public void testUpdateRecipeSuccessfully() throws Exception {
        Long recipeId = 1L;
        RecipeDTO recipeDTO = getDummyUpdatedRecipeDTO();
        LocalDateTime createdDate = LocalDateTime.parse("2023-07-03T18:49:04.61");
        LocalDateTime updatedDate = LocalDateTime.parse("2023-07-03T20:49:04.61");
        recipeDTO.setCreationDate(createdDate);
        recipeDTO.setUpdatedDate(updatedDate);

        Mockito.when(recipeService.updateRecipe(eq(recipeId), any())).thenReturn(recipeDTO);

        mvc.perform(put("/recipe/{id}", recipeId)
                        .content(RECIPE_UPDATE_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Spaghetti 1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.creationDate").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.updatedDate").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.recipeType").value("Vegetarian"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.servingsNumber").value(5))
                .andExpect(MockMvcResultMatchers.jsonPath("$.instructions").value("Enjoy"));
    }

    @Test
    public void testUpdateRecipeThrowRecipeNotFoundException() throws Exception {
        Long recipeId = 100L;
        ErrorInfo errorInfo = new ErrorInfo(ErrorCode.RECIPE_NOT_FOUND_CODE.getCode(),
                String.format(RecipeUtil.RECIPE_NOT_FOUND, recipeId));
        doThrow(new RecipeNotFoundException(errorInfo)).when(recipeService).updateRecipe(eq(recipeId), any());

        mvc.perform(put("/recipe/{id}", recipeId)
                        .content(RECIPE_UPDATE_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(ErrorCode.RECIPE_NOT_FOUND_CODE.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.additionalData.message").value("Recipe 100 - not found"));
    }

    @Test
    public void testDeleteRecipeSuccessfully() throws Exception {
        Long recipeId = 1L;
        mvc.perform(delete("/recipe/{id}", recipeId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteRecipeThrowRecipeNotFoundException() throws Exception {
        Long recipeId = 100L;
        ErrorInfo errorInfo = new ErrorInfo(ErrorCode.RECIPE_NOT_FOUND_CODE.getCode(),
                String.format(RecipeUtil.RECIPE_NOT_FOUND, recipeId));
        doThrow(new RecipeNotFoundException(errorInfo)).when(recipeService)
                .deleteRecipe(recipeId);
        mvc.perform(delete("/recipe/{id}", recipeId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(ErrorCode.RECIPE_NOT_FOUND_CODE.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.additionalData.message").value("Recipe 100 - not found"));
    }

    @Test
    public void testGetRecipeByIdSuccessfully() throws Exception {
        Long recipeId = 1L;
        RecipeDTO recipeDTO = getDummyRecipeDTO();
        recipeDTO.setInstructions("Enjoy");
        recipeDTO.setRecipeType(RecipeType.NON_VEGETARIAN);
        LocalDateTime createdDate = LocalDateTime.parse("2023-07-03T18:49:04.61");
        LocalDateTime updatedDate = LocalDateTime.parse("2023-07-03T20:49:04.61");
        recipeDTO.setCreationDate(createdDate);
        recipeDTO.setUpdatedDate(updatedDate);

        Mockito.when(recipeService.getRecipeById(eq(recipeId))).thenReturn(recipeDTO);

        mvc.perform(get("/recipe/{id}", recipeId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Spaghetti"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.creationDate").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.updatedDate").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.recipeType").value("No Vegetarian"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.servingsNumber").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$.instructions").value("Enjoy"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.ingredients.length()").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.ingredients[0].ingredientId").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.ingredients[0].measurementUnitId").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.ingredients[0].ingredientName").value("Pasta"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.ingredients[0].quantity").value(250f))
                .andExpect(MockMvcResultMatchers.jsonPath("$.ingredients[1].ingredientId").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.ingredients[1].measurementUnitId").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.ingredients[1].ingredientName").value("Water"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.ingredients[1].quantity").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.ingredients[2].ingredientId").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.ingredients[2].measurementUnitId").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.ingredients[2].ingredientName").value("Salt"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.ingredients[2].quantity").value(1));
    }

    @Test
    public void testGetRecipeListSuccessfully() throws Exception {
        int page = 0;
        int size = 10;
        String sort = "name";
        Boolean isVegetarian = true;
        String includedIngredients = "pasta,water";
        String excludedIngredients = "milk";
        String instructionKeyword = "boil";
        Integer servings = 4;
        RecipeDTO recipeDTO = getDummyRecipeDTO();
        LocalDateTime createdDate = LocalDateTime.parse("2023-07-03T18:49:04.61");
        LocalDateTime updatedDate = LocalDateTime.parse("2023-07-03T20:49:04.61");
        recipeDTO.setCreationDate(createdDate);
        recipeDTO.setUpdatedDate(updatedDate);
        PaginatedRecipeResponse pagedResponse = new PaginatedRecipeResponse(List.of(recipeDTO));
        Mockito.when(recipeService.getRecipes(any(), any(), any(), any(), any(), any()))
                .thenReturn(pagedResponse);

        mvc.perform(get("/recipe"
                        + "?page={page}&size={size}&sort={sort}" +
                        "&isVegetarian={isVegetarian}&includedIngredients={includedIngredients}" +
                        "&excludedIngredients={excludedIngredients}" +
                        "&instructionKeyword={instructionKeyword}&servings={servings}",
                        page, size, sort, isVegetarian, includedIngredients,
                        excludedIngredients, instructionKeyword, servings)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].name").value("Spaghetti"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].creationDate").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].updatedDate").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].recipeType").value("Vegetarian"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].servingsNumber").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].instructions").exists());
    }
}

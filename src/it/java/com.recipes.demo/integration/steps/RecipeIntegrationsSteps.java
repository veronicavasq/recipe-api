package com.recipes.demo.integration.steps;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.recipes.demo.exception.ErrorInfo;
import com.recipes.demo.model.PaginatedRecipeResponse;
import com.recipes.demo.model.RecipeDTO;
import com.recipes.demo.model.endpoint.RecipeRequest;
import com.recipes.demo.util.TestUtil;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Strings;
import org.junit.After;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@SpringBootTest
public class RecipeIntegrationsSteps {

    public static final String BASE_PATH = "/recipes";
    private final DataSource dataSource;
    private final RestTemplate restTemplate;

    private ResponseEntity<String> response;

    @LocalServerPort
    private int randomServerPort;

    public RecipeIntegrationsSteps(DataSource dataSource, RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
        this.dataSource = dataSource;
    }

    public static String createLocalApiUrl(
            final int port, final String resourcePath, final String path) {
        return Strings.isNullOrEmpty(path) ? String.format("http://localhost:%1$d/%2$s", port, resourcePath)
                : String.format("http://localhost:%1$d/%2$s/%3$s", port, resourcePath, path);
    }

    @After
    public void tearDown() {
        executeScript("truncate.sql");
    }

    private void executeScript(String fileName) {
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
        databasePopulator.setContinueOnError(true);
        databasePopulator.addScript(new ClassPathResource(fileName));
        DatabasePopulatorUtils.execute(databasePopulator, dataSource);
    }

    @Given("database contains some sample data {string}")
    public void databaseContainsSomeSampleData(String fileName) {
        executeScript(fileName);
    }

    @When("call get recipe list api with path {string}")
    public void callApiWithPath(String path) {
        String apiURI = createLocalApiUrl(randomServerPort, BASE_PATH, path);
        response = restTemplate.getForEntity(apiURI, String.class);
    }

    @Then("api returns {int}")
    public void apiReturnsStatus(int status) {
        Assertions.assertEquals(status, response.getStatusCode().value());
    }

    @And("get recipe list api response have {int} recipes")
    public void getRecipeListApiResponseHaveItemsRecipes(int items) throws JsonProcessingException {
        List<RecipeDTO> list = getRecipeListFromResponse();
        Assertions.assertEquals(list.size(), items);
    }

    @And("get recipe list api response have {string}")
    public void getRecipeListApiResponseHaveRecipeNames(String recipeNames) throws JsonProcessingException {
        List<RecipeDTO> list = getRecipeListFromResponse();
        list.forEach(recipeDTO -> Assertions.assertTrue(recipeNames.contains(recipeDTO.getName())));
    }

    @When("call post recipe api with JSON request")
    public void callPostRecipeApiWithJSONRequest(final String json) throws JsonProcessingException {
        RecipeRequest request = TestUtil.buildObjectMapper().readValue(json, RecipeRequest.class);
        String url = createLocalApiUrl(randomServerPort, BASE_PATH, "recipe");
        try {
            response = restTemplate.postForEntity(url, request, String.class);
        } catch (HttpClientErrorException httpClientErrorException) {
            response = new ResponseEntity<>(httpClientErrorException.getMessage(), httpClientErrorException.getStatusCode());
        }
    }

    @Then("post api returns status {int}")
    public void postApiReturnsStatus(int status) {
        apiReturnsStatus(status);
    }

    @Then("verify post api response has a valid recipeId")
    public void verifyPostApiResponseHasRecipeId() throws JsonProcessingException {
        Assertions.assertTrue(getRecipeDTOFromResponse().getId()>0);
    }

    @And("in post api response recipe name is {string}")
    public void inPostApiResponseRecipeNameIs(String name) throws JsonProcessingException {
        Assertions.assertEquals(getRecipeDTOFromResponse().getName(), name);
    }

    @And("in post api response response recipeType is {string}")
    public void inPostApiResponseRecipeTypeIs(String recipeType) throws JsonProcessingException {
        Assertions.assertEquals(getRecipeDTOFromResponse().getRecipeType().getLabel(), recipeType);
    }

    @And("post api error response has error code {string}")
    public void postApiErrorResponseHasErrorCode(String errorCode) throws JsonProcessingException {
        Assertions.assertEquals(getErrorInfoDTOFromErrorResponse().getCode(), errorCode);
    }

    @And("post api error response has error message {string}")
    public void postApiErrorResponseHasMessage(String message) throws JsonProcessingException {
        Assertions.assertEquals(getErrorInfoDTOFromErrorResponse().getAdditionalData().get("message"), message);
    }

    private List<RecipeDTO> getRecipeListFromResponse() throws JsonProcessingException {
        String body = response.getBody();
        PaginatedRecipeResponse paginatedRecipeResponse = TestUtil.buildObjectMapper().readValue(body, PaginatedRecipeResponse.class);
        return paginatedRecipeResponse.getContent();
    }

    private RecipeDTO getRecipeDTOFromResponse() throws JsonProcessingException {
        String body = response.getBody();
        return TestUtil.buildObjectMapper().readValue(body, RecipeDTO.class);
    }

    private ErrorInfo getErrorInfoDTOFromErrorResponse() throws JsonProcessingException {
        String body = response.getBody();
        Assertions.assertNotNull(body);
        int firstIndex = body.indexOf(":");
        String error = body.substring(firstIndex + 3, body.length() - 1);
        var errorMap = TestUtil.buildObjectMapper().readValue(error, Map.class);
        LinkedHashMap additionalData = (LinkedHashMap) errorMap.get("additionalData");
        return new ErrorInfo((String) errorMap.get("code"), additionalData.get("message"));
    }
}

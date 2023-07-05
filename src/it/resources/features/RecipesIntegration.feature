Feature: Recipe API

  Scenario Outline: check get recipe list
    Given database contains some sample data 'insert_recipes.sql'
    When call get recipe list api with path '<path>'
    Then api returns <status>
    And get recipe list api response have <items> recipes
    And get recipe list api response have '<recipeNames>'


    Examples:

      | path                                                                                                                                  | status | items | recipeNames      |
      | recipe?page=0&size=10&isVegetarian=true&includedIngredients=Banana,Apple&excludedIngredients=Beef&instructionKeyword=Enjoy&servings=4 | 200    | 1     | Recipe           |
      | recipe?page=0&size=10&includedIngredients=Banana,Apple&excludedIngredients=Beef&servings=4                                            | 200    | 2     | Recipe, Recipe 2 |


  Scenario Outline: check save a recipe successfully
    When call post recipe api with JSON request
  """
   {
      "name": "Spaghetti",
      "instructions": "1.Boil water. 2.Add Salt. 3.Add pasta. 4.Drain.",
      "servingsNumber": 4,
      "ingredients": [
       {
        "ingredientId": 1,
        "quantity": 250,
        "measurementUnitId": 1
       },
       {
        "ingredientId": 2,
        "quantity": 1,
        "measurementUnitId": 2
       },
       {
        "ingredientId": 3,
        "quantity": 1,
        "measurementUnitId": 3
       }
       ]
    }
  """
    Then post api returns status <status>
    Then verify post api response has a valid recipeId
    And  in post api response recipe name is '<name>'
    And  in post api response response recipeType is '<recipeType>'
    Examples:
      | status | name      | recipeType |
      | 201    | Spaghetti | Vegetarian |

  Scenario Outline: get error to save a recipe without ingredients
    When call post recipe api with JSON request
  """
   {
      "name": "Spaghetti",
      "instructions": "1.Boil water. 2.Add Salt. 3.Add pasta. 4.Drain.",
      "servingsNumber": 4,
      "ingredients": []
    }
  """
    Then post api returns status <status>
    And post api error response has error code '<errorCode>'
    And post api error response has error message '<message>'
    Examples:
      | status | errorCode | message                                    |
      | 400    | 004       | Recipe should have at least one ingredient |
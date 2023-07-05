DELETE
FROM ingredient_recipe
WHERE ID > 0;
DELETE
FROM recipe
WHERE ID > 0;

INSERT INTO recipe (id, name, instructions, servings_number, recipe_type, creation_date, updated_date)
VALUES (1, 'Recipe', 'Enjoy', '4', 'Vegetarian', '2023-07-03T18:49:04.61', '2023-07-03T18:51:04.61'),
       (2, 'Recipe 2', 'Enjoy cooking', '4', 'No Vegetarian', '2023-07-03T18:49:04.61', '2023-07-03T18:51:04.61');

INSERT INTO ingredient_recipe (id, recipe_id, ingredient_id, measurement_unit_id, quantity)
VALUES (1, 1, 1, 1, 1);
INSERT INTO ingredient_recipe (id, recipe_id, ingredient_id, measurement_unit_id, quantity)
VALUES (2, 1, 2, 2, 1);
INSERT INTO ingredient_recipe (id, recipe_id, ingredient_id, measurement_unit_id, quantity)
VALUES (3, 2, 2, 2, 1);
INSERT INTO ingredient_recipe (id, recipe_id, ingredient_id, measurement_unit_id, quantity)
VALUES (4, 2, 3, 6, 25);
INSERT INTO ingredient_recipe (id, recipe_id, ingredient_id, measurement_unit_id, quantity)
VALUES (5, 2, 1, 2, 1);

INSERT INTO `recipedb`.`ingredient_type` (`name`)
VALUES ('Vegetarian'),
       ('Animal origin');

INSERT INTO `recipedb`.`measurement_unit` (`name`)
VALUES ('Tea Spoon'),
       ('Table Spoon'),
       ('Cup'),
       ('Once'),
       ('Milliliter'),
       ('Gram'),
       ('Kilogram');

INSERT INTO `recipedb`.`ingredient` (`name`, `ingredient_type_id`)
VALUES ('Banana', '1'),
       ('Apple', '1'),
       ('Grape', '1'),
       ('Strawberries', '1'),
       ('Melon', '1'),
       ('Avocado', '1'),
       ('Blueberries', '1'),
       ('Cucumbers', '1'),
       ('Carrot', '1'),
       ('Celery', '1'),
       ('Corn', '1'),
       ('Garlic', '1'),
       ('Tomato', '1'),
       ('Lettuce', '1'),
       ('Onion', '1'),
       ('Ground beaf', '2'),
       ('Chicken', '2'),
       ('Duck', '2'),
       ('Pork', '2'),
       ('Fish', '2'),
       ('Spaghetti', '1'),
       ('Rice', '1');

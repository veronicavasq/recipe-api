DROP TABLE IF EXISTS `ingredient_recipe`;
DROP TABLE IF EXISTS `recipe`;
DROP TABLE IF EXISTS `ingredient`;
DROP TABLE IF EXISTS `measurement_unit`;
DROP TABLE IF EXISTS `ingredient_type`;

CREATE TABLE `ingredient_type`
(
    `id`   int NOT NULL AUTO_INCREMENT,
    `name` varchar(120) DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `sku_UNIQUE` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

CREATE TABLE `ingredient`
(
    `id`                 int          NOT NULL AUTO_INCREMENT,
    `name`               varchar(120) NOT NULL,
    `ingredient_type_id` int          NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `sku_UNIQUE` (`name`),
    CONSTRAINT `ingredient_type_id` FOREIGN KEY (`ingredient_type_id`) REFERENCES `ingredient_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

CREATE TABLE `measurement_unit`
(
    `id`   int          NOT NULL AUTO_INCREMENT,
    `name` varchar(120) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `sku_UNIQUE` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

CREATE TABLE `recipe`
(
    `id`              int          NOT NULL AUTO_INCREMENT,
    `name`            varchar(120) NOT NULL,
    `instructions`    varchar(500) NOT NULL,
    `servings_number` int          NOT NULL,
    `recipe_type`     varchar(50)  NOT NULL,
    `creation_date`   datetime     NOT NULL,
    `updated_date`    datetime NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

CREATE TABLE `ingredient_recipe`
(
    `id`                  int NOT NULL AUTO_INCREMENT,
    `recipe_id`           int NOT NULL,
    `ingredient_id`       int NOT NULL,
    `measurement_unit_id` int NOT NULL,
    `quantity`            float DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY                   `recipe_id_idx` (`recipe_id`),
    KEY                   `ingredient_id_idx` (`ingredient_id`),
    KEY                   `measurement_unit_id_idx` (`measurement_unit_id`),
    CONSTRAINT `recipe_id` FOREIGN KEY (`recipe_id`) REFERENCES `recipe` (`id`),
    CONSTRAINT `ingredient_id` FOREIGN KEY (`ingredient_id`) REFERENCES `ingredient` (`id`),
    CONSTRAINT `measurement_unit_id` FOREIGN KEY (`measurement_unit_id`) REFERENCES `measurement_unit` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

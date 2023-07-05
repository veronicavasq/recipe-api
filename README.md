# Recipes API

Recipes API is a Java application to do CRUD operations on a recipe.

## Table of contents
* [Technologies](#technologies)
* [Architecture](#architecture)
* [Pre-requirements](#pre-requirements)
* [Run project](#run-project)
* [Open API docs](#open-api-docs)

## Technologies

* Java 17
* Spring Boot
* Maven 3+
* Docker
* MySql
* Flyway
* Cucumber
* Testcontainers

## Architecture
Recipes API is implemented as a Java application using Spring framework following the REST principles. The application is composed of hierarchical layers in which each layer has a different behavior/responsibility defined (_Controller_, _Service_, and _Repository_).

The project includes the file (`docker/docker-compose.yml`) that will initiate a container running MySql to make the application easier to run.

Flyway was added to the project to have database schema versioning.

Additionally, integrations test are implemented using Cucumber and Testcontainers. These two technologies help to automate and speed up the process of writing integration tests, which is essential for enterprise applications.

## Pre-requirements

1. Install Docker.
2. Install MySql (Optional).
3. Install Java 17.
4. Install Maven 3+.
5. Install IntelliJ/Eclipse


## Run project

1. Clone project in your local computer:

```
git clone https://github.com/veronicavasq/recipes.git
```

2. If you don't have MySql on your machine, you could run a mysql container with the following commands:
 - Go to the `docker` folder in the project.
   ```
   cd docker
   ```
 - Run the mysql container
   ```
   docker compose up
   ```
 - This steps will initiate a mysql container on port `3306` with user `root` and password `admin`

3. Run the java application using IntelliJ/Eclipse 
```
DemoApplication.java
```

## Open API docs

http://localhost:8080/recipes/swagger-ui/index.html#
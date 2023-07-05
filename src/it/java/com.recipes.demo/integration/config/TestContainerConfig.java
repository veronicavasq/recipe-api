package com.recipes.demo.integration.config;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.junit.AfterClass;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Slf4j
@Testcontainers(parallel = true)
public class TestContainerConfig {

    @Container
    public static MySQLContainer mySQLContainer =
            new MySQLContainer<>("mysql:8.0-debian")
                    .withUsername("test")
                    .withPassword("test")
                    .withDatabaseName("recipedb");

    @AfterClass
    public static void clean() {
        mySQLContainer.stop();
    }

    public static class Initializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(final @NotNull ConfigurableApplicationContext applicationContext) {
            mySQLContainer.start();

            TestPropertyValues values =
                    TestPropertyValues.of(
                            "spring.datasource.password=" + mySQLContainer.getPassword(),
                            "spring.datasource.username=" + mySQLContainer.getUsername(),
                            "spring.datasource.url=" + mySQLContainer.getJdbcUrl(),
                            "spring.flyway.url=" + mySQLContainer.getJdbcUrl(),
                            "spring.flyway.user=" + mySQLContainer.getUsername(),
                            "spring.flyway.password=" + mySQLContainer.getPassword());

            values.applyTo(applicationContext);
        }
    }
}
package com.task.socialnetwork.controller;

import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public class AbstractIntegrationTest {

  // Define a static PostgreSQL container that will start before tests and shut down after tests
  private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>(
      DockerImageName.parse("postgres:15.3-alpine")
  )
      .withDatabaseName("testdb")
      .withUsername("testuser")
      .withPassword("testpass");

  @BeforeAll
  static void startContainer() {
    postgresContainer.start();

    // Set Spring DataSource properties from the container
    System.setProperty("spring.datasource.url", postgresContainer.getJdbcUrl());
    System.setProperty("spring.datasource.username", postgresContainer.getUsername());
    System.setProperty("spring.datasource.password", postgresContainer.getPassword());
  }

}

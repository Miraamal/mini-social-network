package com.task.socialnetwork.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Integration tests for StatisticsController.
 * Assumes that the database is populated with some posts and users.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
class StatisticsControllerIntegrationTest extends AbstractIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  @WithMockUser(username = "regularUser", roles = {"USER"})
  void testGetPopularPostsAsUser() throws Exception {
    mockMvc.perform(get("/api/statistics/popular-posts?limit=5"))
        .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(username = "adminUser", roles = {"ADMIN"})
  void testGetUserActivityAsAdmin() throws Exception {
    // Assuming user with ID 1 exists
    mockMvc.perform(get("/api/statistics/user-activity?userId=1"))
        .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(username = "regularUser", roles = {"USER"})
  void testGetUserActivityAsNonAdmin() throws Exception {
    mockMvc.perform(get("/api/statistics/user-activity?userId=1"))
        .andExpect(status().isForbidden());
  }
}

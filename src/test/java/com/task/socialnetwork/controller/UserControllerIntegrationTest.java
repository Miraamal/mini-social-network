package com.task.socialnetwork.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.socialnetwork.dto.AuthRequestDTO;
import com.task.socialnetwork.dto.AuthResponseDTO;
import com.task.socialnetwork.dto.UserDTO;
import com.task.socialnetwork.dto.UserUpdateRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
class UserControllerIntegrationTest extends AbstractIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  private String authToken;
  private Long testUserId = 1L; // Adjust this ID based on your test data

  @BeforeEach
  void setUp() throws Exception {
    // Get a token for existingUser (ROLE_USER), assuming existingUser has ID = 1
    AuthRequestDTO loginRequest = new AuthRequestDTO();
    loginRequest.setUsername("admin");
    loginRequest.setPassword("admin");

    String response = mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();

    AuthResponseDTO authResponse = objectMapper.readValue(response, AuthResponseDTO.class);
    authToken = authResponse.getToken();
    assertThat(authToken).isNotNull();
  }

  @Test
  void testGetUserById() throws Exception {
    mockMvc.perform(get("/api/user/100")
            .header("Authorization", "Bearer " + authToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(100));
  }

  @Test
  void testDeleteUserAsOwner() throws Exception {
    mockMvc.perform(delete("/api/user/" + testUserId)
            .header("Authorization", "Bearer " + authToken))
        .andExpect(status().isNoContent());
  }

  @Test
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  void testGetAllUsersAsAdmin() throws Exception {
    mockMvc.perform(get("/api/user")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  void testUpdateUserProfileAsOwner() throws Exception {
    UserUpdateRequestDTO updateRequest = new UserUpdateRequestDTO();
    updateRequest.setUsername("updatedName");
    updateRequest.setEmail("updated@example.com");

    String response = mockMvc.perform(put("/api/user/" + testUserId)
            .header("Authorization", "Bearer " + authToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();

    UserDTO updatedUser = objectMapper.readValue(response, UserDTO.class);
    assertThat(updatedUser.getUsername()).isEqualTo("updatedName");
  }

}

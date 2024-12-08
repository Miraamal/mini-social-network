package com.task.socialnetwork.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.socialnetwork.dto.AuthRequestDTO;
import com.task.socialnetwork.dto.AuthResponseDTO;
import com.task.socialnetwork.dto.RegistrationRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
class AuthControllerIntegrationTest extends AbstractIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  // This user should not exist before registration in the test DB
  private RegistrationRequestDTO registrationRequest;
  // This user should exist in the DB (configured via data.sql or your initial data)
  private AuthRequestDTO loginRequest;

  @BeforeEach
  void setUp() {
    registrationRequest = new RegistrationRequestDTO();
    registrationRequest.setUsername("newUser");
    registrationRequest.setEmail("newuser@example.com");
    registrationRequest.setPassword("password123");

    loginRequest = new AuthRequestDTO();
    loginRequest.setUsername("admin");
    loginRequest.setPassword("admin");
  }

  @Test
  void testRegisterUser() throws Exception {
    mockMvc.perform(post("/api/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registrationRequest)))
        .andExpect(status().isOk())
        .andExpect(content().string("User registered successfully"));
  }

  @Test
  void testLoginUser() throws Exception {
    String response = mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").exists())
        .andReturn().getResponse().getContentAsString();

    AuthResponseDTO authResponse = objectMapper.readValue(response, AuthResponseDTO.class);
    assertThat(authResponse.getToken()).isNotBlank();
  }
}

package com.task.socialnetwork.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.socialnetwork.dto.AddCommentDTO;
import com.task.socialnetwork.dto.AuthRequestDTO;
import com.task.socialnetwork.dto.AuthResponseDTO;
import com.task.socialnetwork.dto.PostResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
class PostControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String authToken;
    private Long createdPostId;

    @BeforeEach
    void setUp() throws Exception {
        // Obtain token
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
    void testCreatePost() throws Exception {
        MockMultipartFile image = new MockMultipartFile("image", "test_image.png",
            MediaType.TEXT_PLAIN_VALUE, "ImageBytes".getBytes());

       String response = mockMvc.perform(multipart("/api/posts")
                .file(image)
                .param("content", "My first post") // Providing the required content param
                .header("Authorization", "Bearer " + authToken))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.content").value("My first post"))
           .andReturn().getResponse().getContentAsString();

        PostResponseDTO responseDTO = objectMapper.readValue(response, PostResponseDTO.class);
        createdPostId = responseDTO.getId();
    }

    @Test
    void testGetPostAfterCreation() throws Exception {
        // Ensure post is created first by calling testCreatePost or pre-inserting data
        if (createdPostId == null) {
            testCreatePost();
        }

        mockMvc.perform(get("/api/posts/" + createdPostId)
                .header("Authorization", "Bearer " + authToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(createdPostId));
    }

    @Test
    void testLikePost() throws Exception {
        if (createdPostId == null) {
            testCreatePost();
        }

        mockMvc.perform(post("/api/posts/" + createdPostId + "/like")
                .header("Authorization", "Bearer " + authToken))
            .andExpect(status().isOk());
    }

    @Test
    void testAddCommentToPost() throws Exception {
        if (createdPostId == null) {
            testCreatePost();
        }

        AddCommentDTO comment = new AddCommentDTO();
        comment.setContent("Nice post!");

        mockMvc.perform(post("/api/posts/" + createdPostId + "/comments")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(comment)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.content").value("Nice post!"));
    }

    @Test
    void testGetNewsFeed() throws Exception {
        mockMvc.perform(get("/api/posts/feed")
                .header("Authorization", "Bearer " + authToken)
                .param("filter", "TIME"))
            .andExpect(status().isOk());
    }
}

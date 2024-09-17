package com.springboot.blog.Post;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.blog.controller.PostController;

import com.springboot.blog.exception.ResourceNotFoundException;

import com.springboot.blog.payload.PostDto;
import com.springboot.blog.payload.PostResponse;
import com.springboot.blog.service.PostService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;



import java.util.List;
import java.util.Set;
import java.util.stream.Stream;


import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"test"})
public class PostControllerTest {
    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    PostService postService;

    @Test
    @WithMockUser(username = "Javi", roles = {"ADMIN"})
    void createPostTest_201() throws Exception {
        PostDto postDto = new PostDto();
        postDto.setId(1L);
        postDto.setTitle("title");
        postDto.setContent("content");
        postDto.setDescription("description");
        postDto.setComments(Set.of());
        postDto.setCategoryId(1L);
        when(postService.createPost(any(PostDto.class))).thenReturn(postDto);
        mockMvc.perform(post("/api/posts")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(postDto)).accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated());
        MvcResult mvcResult = mockMvc.perform(post("/api/posts")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(postDto)).accept(MediaType.APPLICATION_JSON)
        ).andReturn();
        String actualResponse = mvcResult.getResponse().getContentAsString();
        assertThat(actualResponse).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(postDto));
    }
    
    @Test
    @WithMockUser(username = "Pedro", roles = {"ADMIN"})
    void updatePost_201() throws Exception{
        PostDto postDto = new PostDto();
        postDto.setId(1L);
        postDto.setTitle("title");
        postDto.setContent("sin editar");
        postDto.setDescription("description");
        postDto.setComments(Set.of());
        postDto.setCategoryId(1L);

        PostDto porDtoEdicion = new PostDto();
        porDtoEdicion.setId(1L);
        porDtoEdicion.setTitle("title");
        porDtoEdicion.setContent("editado");
        porDtoEdicion.setDescription("description");
        porDtoEdicion.setComments(Set.of());
        porDtoEdicion.setCategoryId(1L);

        when(postService.updatePost(Mockito.any(PostDto.class), eq(postDto.getId()))).thenReturn(porDtoEdicion);
        mockMvc.perform(put("/api/posts/{id}", postDto.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postDto)).accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        MvcResult result = mockMvc.perform(put("/api/posts/{id}", postDto.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(porDtoEdicion)).accept(MediaType.APPLICATION_JSON)).andReturn();
        String response = result.getResponse().getContentAsString();
        assertThat(response).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(porDtoEdicion));


    }

    @Test
    @WithMockUser(username = "Pedro", roles = {"ADMIN"})
    void updatePost_400_tileEmpty() throws Exception{
        PostDto postDto = new PostDto();
        postDto.setId(1L);
        postDto.setContent("sin editar");
        postDto.setDescription("description");
        postDto.setComments(Set.of());
        postDto.setCategoryId(1L);

        PostDto porDtoEdicion = new PostDto();
        porDtoEdicion.setId(1L);
        porDtoEdicion.setTitle("title");
        porDtoEdicion.setContent("editado");
        porDtoEdicion.setDescription("description");
        porDtoEdicion.setComments(Set.of());
        porDtoEdicion.setCategoryId(1L);

        when(postService.updatePost(Mockito.any(PostDto.class), eq(postDto.getId()))).thenReturn(porDtoEdicion);
        mockMvc.perform(put("/api/posts/{id}", postDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postDto)).accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "Pedro", roles = {"ADMIN"})
    void updatePost_400_postNotExists() throws Exception{
        PostDto postDto = new PostDto();
        postDto.setId(1L);
        postDto.setContent("sin editar");
        postDto.setDescription("description");
        postDto.setComments(Set.of());
        postDto.setCategoryId(1L);

        PostDto porDtoEdicion = new PostDto();
        porDtoEdicion.setId(0L);
        porDtoEdicion.setTitle("title");
        porDtoEdicion.setContent("editado");
        porDtoEdicion.setDescription("description");
        porDtoEdicion.setComments(Set.of());
        porDtoEdicion.setCategoryId(1L);

        when(postService.updatePost(Mockito.any(PostDto.class), eq(postDto.getId()))).thenReturn(porDtoEdicion);
        mockMvc.perform(put("/api/posts/{id}", postDto.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postDto)).accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "Pedro", roles = {"USER"})
    void updatePost_403_forbiddenAccess() throws Exception {
        PostDto postDto = new PostDto();
        postDto.setId(1L);
        postDto.setTitle("Title");
        postDto.setContent("content");
        postDto.setDescription("description");
        postDto.setComments(Set.of());
        postDto.setCategoryId(1L);

        mockMvc.perform(put("/api/posts/{id}", postDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithMockUser(username = "Javi")
    void createPostTest_401() throws Exception {
        PostDto postDto = new PostDto();
        postDto.setId(1L);
        postDto.setTitle("title");
        postDto.setContent("content");
        postDto.setDescription("description");
        postDto.setComments(Set.of());
        postDto.setCategoryId(1L);
        mockMvc.perform(post("/api/posts")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(postDto)).accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnauthorized());
    }

    public static Stream<Arguments> getData() {
        PostDto badTitle = new PostDto();
        badTitle.setCategoryId(1L);
        badTitle.setTitle("h");
        badTitle.setDescription("description dam");
        badTitle.setId(1L);
        badTitle.setContent("f");
        PostDto badDesc= new PostDto();
        badDesc.setCategoryId(1L);
        badDesc.setTitle("hfdasfdsa");
        badDesc.setDescription("d");
        badDesc.setId(1L);
        badDesc.setContent("f");
        PostDto empty = new PostDto();
        return Stream.of(
                Arguments.of(badTitle),
                Arguments.of(badDesc),
                Arguments.of(empty)
        );
    }
    @ParameterizedTest
    @MethodSource({"getData"})
    @WithMockUser(username = "Javi", roles = {"ADMIN"})
    void createPostTest_400(PostDto postDto) throws Exception {

        mockMvc.perform(post("/api/posts")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(postDto)).accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void getAllPostTest_200() throws Exception {
        PostResponse postResponse = new PostResponse(List.of(), 0, 10, 0, 0, true);
        when(postService.getAllPosts(0, 10, "id", "asc")).thenReturn(postResponse);
        mockMvc.perform(get("/api/posts")
                .contentType("application/json")).andExpect(status().isOk());
        MvcResult mvcResult = mockMvc.perform(get("/api/posts")
                .contentType("application/json")).andReturn();
        String actualResponse = mvcResult.getResponse().getContentAsString();
        assertThat(actualResponse).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(postResponse));
    }

    @Test
    void getPostById_200() throws Exception {
        PostDto postDto = new PostDto();
        postDto.setContent("content");
        postDto.setComments(Set.of());
        postDto.setId(1L);
        postDto.setDescription("description");
        postDto.setTitle("title");
        postDto.setCategoryId(1L);
        when(postService.getPostById(any(Long.class))).thenReturn(postDto);
        mockMvc.perform(get("/api/posts/1")
                .contentType("application/json")).andExpect(status().isOk());
        MvcResult mvcResult = mockMvc.perform(get("/api/posts/1")
                .contentType("application/json")).andReturn();
        String actualResponse = mvcResult.getResponse().getContentAsString();
        assertThat(actualResponse).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(postDto));
    }

    @Test
    void getPostById_404() throws Exception {
        when(postService.getPostById(any(Long.class))).thenThrow(new ResourceNotFoundException("Post", "id", 1));
        mockMvc.perform(get("/api/posts/1")
                .contentType("application/json")).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "Javi", roles = {"ADMIN"})
    void whenDeletePost_then200() throws Exception{
        mockMvc.perform(delete("/api/posts/1")
                .contentType("application/json")).andExpect(status().isOk());
        verify(postService, atLeastOnce()).deletePostById(any(Long.class));
    }
    @Test
    @WithMockUser()
    void whenDeletePost_then401() throws Exception{
        mockMvc.perform(delete("/api/posts/1")
                .contentType("application/json")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "Javi", roles = {"ADMIN"})
    void whenDeletePost_then404() throws Exception{
        doThrow(new ResourceNotFoundException("Post", "id", 1)).when(postService).deletePostById(any(Long.class));
        mockMvc.perform(delete("/api/posts/1")
                .contentType("application/json")).andExpect(status().isNotFound());
    }

    @Test
    void whenGetPostsByCategory_then200() throws Exception{
        PostDto postDto = new PostDto();
        postDto.setCategoryId(1L);
        postDto.setContent("h");
        postDto.setId(1L);
        postDto.setComments(Set.of());
        postDto.setDescription("fe");
        postDto.setTitle("fdsfes");
        when(postService.getPostsByCategory(any(Long.class))).thenReturn(List.of(postDto));
        mockMvc.perform(get("/api/posts/category/1")
                .contentType("application/json")).andExpect(status().isOk());
        MvcResult mvcResult = mockMvc.perform(get("/api/posts/category/1")
                .contentType("application/json")).andReturn();

        String actualResponse = mvcResult.getResponse().getContentAsString();
        assertThat(actualResponse).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(List.of(postDto)));
    }

    @Test
    void whenGetPostsByCategory_then404() throws Exception{
        ResourceNotFoundException exception = new ResourceNotFoundException("Category", "id", 1);
        when(postService.getPostsByCategory(any(Long.class))).thenThrow(exception);
        mockMvc.perform(get("/api/posts/category/1")
                .contentType("application/json"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(exception.getMessage()));
    }



}


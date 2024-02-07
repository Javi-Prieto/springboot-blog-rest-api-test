package com.springboot.blog.Comment;

import com.springboot.blog.entity.Comment;
import com.springboot.blog.entity.Post;
import com.springboot.blog.payload.CommentDto;
import com.springboot.blog.service.CommentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.shaded.com.fasterxml.jackson.core.type.TypeReference;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;


import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CommentControllerTest {
//
    @Autowired
    private MockMvc mockMvc;

    ObjectMapper objectMapper = new ObjectMapper();
    @MockBean
    private CommentService commentService;



    @Test
    @WithMockUser(username = "Javi", roles = {"ADMIN"})
    void testCreateComment() throws  Exception{

        Comment comment = new Comment(1L,"hola","angel@gmail","bodyyyyyyyyyyyyy",new Post());
        long postId = 1L;
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setName(comment.getName());
        commentDto.setEmail(comment.getEmail());
        commentDto.setBody(comment.getBody());
        when(commentService.createComment(any(Long.class),any(CommentDto.class))).thenReturn(commentDto);
        mockMvc.perform(post("/api/v1/posts/"+postId+"/comments")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(commentDto)).accept(APPLICATION_JSON)).andExpect(status().isCreated());

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/posts/"+postId+"/comments")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(commentDto)).accept(APPLICATION_JSON)).andReturn();

        String resultado = mvcResult.getResponse().getContentAsString();
        assertThat(resultado).isEqualToIgnoringCase(objectMapper.writeValueAsString(commentDto));
    }

    @Test
    @WithMockUser(username = "Javi", roles = {"ADMIN"})
    void testCreateComment400() throws  Exception{
        Comment comment = new Comment(1L,"angel","angel@gmail","body",new Post());
        long postId = 1L;
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setName(comment.getName());
        commentDto.setEmail(comment.getEmail());
        commentDto.setBody(comment.getBody());
        when(commentService.createComment(any(Long.class),any(CommentDto.class))).thenReturn(commentDto);
        mockMvc.perform(post("/api/v1/posts/"+postId+"/comments")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(commentDto)).accept(APPLICATION_JSON)).andExpect(status().isBadRequest());

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/posts/"+postId+"/comments")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(commentDto)).accept(APPLICATION_JSON)).andReturn();

        String resultado = mvcResult.getResponse().getContentAsString();
        assertThat(resultado);
    }
    @Test
    @WithMockUser(username = "Javi", roles = {"ADMIN"})
    void testCreateCommentWithInvalidData() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setName("");
        commentDto.setEmail("angel@gmail");
        commentDto.setBody("bodyyyyyyyyyyyyy");

        mockMvc.perform(post("/api/v1/posts/" + 1L + "/comments")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(commentDto))
                        .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
    @Test
    @WithMockUser(username = "Javi", roles = {"ADMIN"})
    void testGetByPostIdComment() throws  Exception{
        long postId = 1L;
        Comment comment = new Comment(1L,"hola","angel@gmail","bodyyyyyyyyyyyyy",new Post());
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setName(comment.getName());
        commentDto.setEmail(comment.getEmail());
        commentDto.setBody(comment.getBody());
        when(commentService.getCommentsByPostId(any(Long.class))).thenReturn(List.of(commentDto));

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/posts/"+postId+"/comments")
                .contentType("application/json")
                .accept(APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

        String resultado = mvcResult.getResponse().getContentAsString();
        List<CommentDto> resultadoDto = objectMapper.readValue(resultado, new TypeReference<List<CommentDto>>(){});

        assertEquals(List.of(commentDto), resultadoDto);


    }
    @Test
    @WithMockUser(username = "Javi", roles = {"ADMIN"})
    void testGetCommentsByPostIdWithNoComments() throws Exception {
        when(commentService.getCommentsByPostId(any(Long.class)))
                .thenReturn(Collections.emptyList());

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/posts/" + 1L + "/comments")
                        .contentType("application/json")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String resultado = mvcResult.getResponse().getContentAsString();
        List<CommentDto> resultadoDto = objectMapper.readValue(resultado, new TypeReference<List<CommentDto>>(){});

        assertTrue(resultadoDto.isEmpty());
    }
    @Test
    @WithMockUser(username = "Javi", roles = {"ADMIN"})
    void testGetCommentsByPostIdWithInvalidPostId() throws Exception {
        when(commentService.getCommentsByPostId(any(Long.class)))
                .thenReturn(Collections.emptyList());

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/posts/" + 9999L + "/comments")
                        .contentType("application/json")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String resultado = mvcResult.getResponse().getContentAsString();
        List<CommentDto> resultadoDto = objectMapper.readValue(resultado, new TypeReference<List<CommentDto>>(){});

        assertTrue(resultadoDto.isEmpty());
    }

    @Test
    @WithMockUser(username = "Javi", roles = {"ADMIN"})
    void testGetCommentById() throws Exception{
        long postId = 1L;
        long commentId = 1L;
        Comment comment = new Comment(1L,"hola","angel@gmail","bodyyyyyyyyyyyyy",new Post());
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setName(comment.getName());
        commentDto.setEmail(comment.getEmail());
        commentDto.setBody(comment.getBody());
        when(commentService.getCommentById(any(Long.class),any(Long.class))).thenReturn(commentDto);
        MvcResult mvcResult = mockMvc.perform(get("/api/v1/posts/"+postId+"/comments/"+commentId)
                .contentType("application/json")
                .accept(APPLICATION_JSON)).andExpect(status().isOk()).andReturn();
        String resultado = mvcResult.getResponse().getContentAsString();
        assertThat(resultado).isEqualToIgnoringCase(objectMapper.writeValueAsString(commentDto));


    }
    //

    @Test
    @WithMockUser(username = "Javi", roles = {"ADMIN"})
    void testUpdateComment () throws Exception{
        long postId = 1L;
        long commentId = 1L;
        Comment comment = new Comment(1L,"hola","angel@gmail","bodyyyyyyyyyyyyy",new Post());
        CommentDto commentDto = new CommentDto();

        commentDto.setName(comment.getName());
        commentDto.setEmail(comment.getEmail());
        commentDto.setBody(comment.getBody());
        when(commentService.updateComment(any(Long.class),any(Long.class),any(CommentDto.class))).thenReturn(commentDto);

        MvcResult mvcResult = mockMvc.perform(put("/api/v1/posts/"+postId+"/comments/"+commentId)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(commentDto))
                .accept(APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

        String resultado = mvcResult.getResponse().getContentAsString();

        assertThat(resultado).isEqualToIgnoringCase(objectMapper.writeValueAsString(commentDto));
    }
    @Test
    @WithMockUser(username = "Javi", roles = {"ADMIN"})
    void testUpdateCommentWithInvalidData() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setName("");
        commentDto.setEmail("angel@gmail");
        commentDto.setBody("bodyyyyyyyyyyyyy");

        mockMvc.perform(put("/api/v1/posts/" + 1L + "/comments/" + 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(commentDto))
                        .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
    @Test
    @WithMockUser(username = "Javi", roles = {"ADMIN"})
    void testDeleteComment() throws Exception{
        long postId = 1L;
        long commentId = 1L;
        Comment comment = new Comment(1L,"hola","angel@gmail","bodyyyyyyyyyyyyy",new Post());
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setName(comment.getName());
        commentDto.setEmail(comment.getEmail());
        commentDto.setBody(comment.getBody());
        String mensage = "ok";
        doNothing().when(commentService).deleteComment(any(Long.class), any(Long.class));

        MvcResult mvcResult = mockMvc.perform(delete("/api/v1/posts/"+postId+"/comments/"+commentId)
                .contentType("application/json")
                .accept(APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

        String resultado = mvcResult.getResponse().getContentAsString();

        verify(commentService).deleteComment(postId,commentId);

    }






}

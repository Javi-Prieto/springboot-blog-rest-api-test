package com.springboot.blog.Comment;

import com.springboot.blog.entity.Post;
import com.springboot.blog.payload.CommentDto;
import com.springboot.blog.payload.PostDto;
import com.springboot.blog.payload.PostResponse;
import com.springboot.blog.repository.UserRepository;
import com.springboot.blog.security.JwtTokenProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles({"test"})
@Testcontainers
@Sql(value = "classpath:import-integration-comment.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class CoomentIntegration {

    @Autowired
    TestRestTemplate testRestTemplate;
    @Autowired
    JwtTokenProvider jwtProvider;
    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer(DockerImageName.parse("postgres:16-alpine"))
            .withUsername("user")
            .withPassword("user")
            .withDatabaseName("test");
    @LocalServerPort
    int port;

    String adminToken;
    String userToken;

    CommentDto commentDto =  new CommentDto();


    @BeforeEach
    void setUp(){
        testRestTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());

        Collection<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"));
        Authentication auth = new UsernamePasswordAuthenticationToken("lacabra_7","lacabra", authorities);
        adminToken = jwtProvider.generateToken(auth);
        Collection<GrantedAuthority> authorities2 = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
        Authentication auth2 = new UsernamePasswordAuthenticationToken("ToRechulon","1234", authorities2);
        userToken = jwtProvider.generateToken(auth2);

       commentDto.setName("angel");
       commentDto.setBody("bodyyyyyyyyyyyyyyy");
       commentDto.setEmail("angel@gmail");
       commentDto.setId(1L);
    }

    @Test
    void createComment(){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminToken);
        HttpEntity<Object> requestBodyHeaders = new HttpEntity<>(commentDto, headers);
        ResponseEntity<CommentDto> response = testRestTemplate.exchange("/api/v1/posts/"+1000L+"/comments", HttpMethod.POST, requestBodyHeaders, CommentDto.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }
    @Test
    void createCommentWithInvalidPostId(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminToken);
        HttpEntity<Object> requestBodyHeaders = new HttpEntity<>(commentDto, headers);
        ResponseEntity<CommentDto> response = testRestTemplate.exchange("/api/v1/posts/"+9999L+"/comments", HttpMethod.POST, requestBodyHeaders, CommentDto.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void createCommentWithInvalidData(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminToken);
        commentDto.setEmail("invalid email");
        HttpEntity<Object> requestBodyHeaders = new HttpEntity<>(commentDto, headers);
        ResponseEntity<CommentDto> response = testRestTemplate.exchange("/api/v1/posts/"+1000L+"/comments", HttpMethod.POST, requestBodyHeaders, CommentDto.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getCommentByIdPostTest(){
        ResponseEntity<List<CommentDto>> response = testRestTemplate.exchange(
                "/api/v1/posts/"+1000L+"/comments",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<CommentDto>>() {}
        );

        List<CommentDto> commentDtos = response.getBody();

        assertFalse(commentDtos.isEmpty());
        assertEquals(1, commentDtos.size());
        assertEquals("angel", commentDtos.get(0).getName());
        assertEquals("bodyyyyyyyyyyyyyyy", commentDtos.get(0).getBody());
        assertEquals("angel@gmail.com", commentDtos.get(0).getEmail());
        assertEquals(1L, commentDtos.get(0).getId());
    }
    @Test
    void getCommentByIdTest(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminToken);
        HttpEntity<Object> requestBodyHeaders = new HttpEntity<>(headers);
        ResponseEntity<CommentDto> response = testRestTemplate.exchange("/api/v1/posts/"+1000L+"/comments/"+1L, HttpMethod.GET, requestBodyHeaders, CommentDto.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
    @Test
    void getCommentByIdWithInvalidPostIdTest(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminToken);
        HttpEntity<Object> requestBodyHeaders = new HttpEntity<>(headers);
        ResponseEntity<CommentDto> response = testRestTemplate.exchange("/api/v1/posts/"+9999L+"/comments/"+1L, HttpMethod.GET, requestBodyHeaders, CommentDto.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void updateCommentTest(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminToken);
        commentDto.setName("Nuevo nombre");
        commentDto.setBody("Nuevo cuerpo");
        HttpEntity<Object> requestBodyHeaders = new HttpEntity<>(commentDto, headers);
        ResponseEntity<CommentDto> response = testRestTemplate.exchange("/api/v1/posts/"+1000L+"/comments/"+1L, HttpMethod.PUT, requestBodyHeaders, CommentDto.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Nuevo nombre", response.getBody().getName());
        assertEquals("Nuevo cuerpo", response.getBody().getBody());
    }
    @Test
    void updateCommentWithInvalidPostIdTest(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminToken);
        commentDto.setName("Nuevo nombre");
        commentDto.setBody("Nuevo cuerpo");
        HttpEntity<Object> requestBodyHeaders = new HttpEntity<>(commentDto, headers);
        ResponseEntity<CommentDto> response = testRestTemplate.exchange("/api/v1/posts/"+9999L+"/comments/"+1L, HttpMethod.PUT, requestBodyHeaders, CommentDto.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void updateCommentWithInvalidDataTest(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminToken);
        commentDto.setName(""); 
        HttpEntity<Object> requestBodyHeaders = new HttpEntity<>(commentDto, headers);
        ResponseEntity<CommentDto> response = testRestTemplate.exchange("/api/v1/posts/"+1000L+"/comments/"+1L, HttpMethod.PUT, requestBodyHeaders, CommentDto.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    @Test
    void deleteCommentTest(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminToken);
        HttpEntity<Object> requestBodyHeaders = new HttpEntity<>(headers);
        ResponseEntity<String> response = testRestTemplate.exchange("/api/v1/posts/"+1000L+"/comments/"+1L, HttpMethod.DELETE, requestBodyHeaders, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Comment deleted successfully", response.getBody());
    }

    @Test
    void deleteCommentWithInvalidPostIdTest(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminToken);
        HttpEntity<Object> requestBodyHeaders = new HttpEntity<>(headers);
        ResponseEntity<String> response = testRestTemplate.exchange("/api/v1/posts/"+9999L+"/comments/"+1L, HttpMethod.DELETE, requestBodyHeaders, String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteCommentWithInvalidCommentIdTest(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminToken);
        HttpEntity<Object> requestBodyHeaders = new HttpEntity<>(headers);
        ResponseEntity<String> response = testRestTemplate.exchange("/api/v1/posts/"+1000L+"/comments/"+9999L, HttpMethod.DELETE, requestBodyHeaders, String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }



}

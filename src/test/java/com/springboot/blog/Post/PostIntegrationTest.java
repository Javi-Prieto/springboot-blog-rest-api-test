package com.springboot.blog.Post;

import com.springboot.blog.entity.Post;
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
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles({"integration-test"})
@Sql(value = "classpath:import-integration-post.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class PostIntegrationTest {

    @Autowired
    TestRestTemplate testRestTemplate;
    @Autowired
    JwtTokenProvider jwtProvider;

    @LocalServerPort
    int port;

    String adminToken;
    String userToken;
    PostDto postDto = new PostDto();
    @BeforeEach
    void setUp(){
        testRestTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());

        Collection<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"));
        Authentication auth = new UsernamePasswordAuthenticationToken("lacabra_7","lacabra", authorities);
        adminToken = jwtProvider.generateToken(auth);
        Collection<GrantedAuthority> authorities2 = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
        Authentication auth2 = new UsernamePasswordAuthenticationToken("ToRechulon","1234", authorities2);
        userToken = jwtProvider.generateToken(auth2);

        postDto.setTitle("title");
        postDto.setDescription("description");
        postDto.setContent("contenidos guapos");
        postDto.setComments(Set.of());
        postDto.setCategoryId(1000L);
        postDto.setId(1);
    }

    @Test
    void whenCreatePost_then201(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminToken);
        HttpEntity<Object> requestBodyHeaders = new HttpEntity<>(postDto, headers);
        ResponseEntity<PostDto> response = testRestTemplate.exchange("/api/posts", HttpMethod.POST, requestBodyHeaders, PostDto.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void whenCreatePost_then401(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(userToken);
        HttpEntity<Object> requestBodyHeaders = new HttpEntity<>(postDto, headers);
        ResponseEntity<PostDto> response = testRestTemplate.exchange("/api/posts", HttpMethod.POST, requestBodyHeaders, PostDto.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void whenCreatePost_then400(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminToken);
        HttpEntity<Object> requestBodyHeaders = new HttpEntity<>(new PostDto(), headers);
        ResponseEntity<PostDto> response = testRestTemplate.exchange("/api/posts", HttpMethod.POST, requestBodyHeaders, PostDto.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    @Test
    void whenCreatePost_then404(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminToken);
        postDto.setCategoryId(1L);
        HttpEntity<Object> requestBodyHeaders = new HttpEntity<>(postDto, headers);
        ResponseEntity<PostDto> response = testRestTemplate.exchange("/api/posts", HttpMethod.POST, requestBodyHeaders, PostDto.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void whenGetAllPost_then200(){
        PostResponse postResponse = testRestTemplate.getForObject("/api/posts", PostResponse.class);
        assertEquals(2, postResponse.getContent().size());
    }

    @Test
    void whenGetPostById_then200(){
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "application/json");
        HttpEntity<Object> responseHeaders = new HttpEntity<>(headers);
        ResponseEntity<PostDto> response = testRestTemplate.exchange("/api/posts/{id}", HttpMethod.GET, responseHeaders, PostDto.class, 1000);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("hola mundo java", response.getBody().getDescription());
    }
    @Test
    void whenGetPostById_then404(){
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "application/json");
        HttpEntity<Object> responseHeaders = new HttpEntity<>(headers);
        ResponseEntity<PostDto> response = testRestTemplate.exchange("/api/posts/{id}", HttpMethod.GET, responseHeaders, PostDto.class, 300);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
    @Test
    void whenDeletePost_then200(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminToken);
        HttpEntity<Object> responseHeaders = new HttpEntity<>(headers);
        ResponseEntity<String> response = testRestTemplate.exchange("/api/posts/{id}", HttpMethod.DELETE, responseHeaders, String.class, 1001);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
    @Test
    void whenDeletePost_then404(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminToken);
        HttpEntity<Object> responseHeaders = new HttpEntity<>(headers);
        ResponseEntity<String> response = testRestTemplate.exchange("/api/posts/{id}", HttpMethod.DELETE, responseHeaders, String.class, 543809584);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void whenDeletePost_then401(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(userToken);
        HttpEntity<Object> responseHeaders = new HttpEntity<>(headers);
        ResponseEntity<String> response = testRestTemplate.exchange("/api/posts/{id}", HttpMethod.DELETE, responseHeaders, String.class, 1001);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void whenGetPostsByCategory_then200(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(userToken);
        HttpEntity<Object> responseHeaders = new HttpEntity<>(headers);
        ResponseEntity<PostDto[]> response = testRestTemplate.exchange("/api/posts/category/{id}", HttpMethod.GET, responseHeaders, PostDto[].class, 1000);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().length);
    }

    @Test
    void whenGetPostsByCategory_then404(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(userToken);
        HttpEntity<Object> responseHeaders = new HttpEntity<>(headers);
        ResponseEntity<String> response = testRestTemplate.exchange("/api/posts/category/{id}", HttpMethod.GET, responseHeaders, String.class, 1);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }


    @Test
    void whebUpdatePost_then200(){
        PostDto postDto = new PostDto();
        postDto.setId(1001);
        postDto.setDescription("Description");
        postDto.setContent("contents");
        postDto.setTitle("Title");
        postDto.setCategoryId(1000L);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminToken);
        HttpEntity<PostDto> responseHeader = new HttpEntity<>(postDto, headers);
        ResponseEntity<PostDto> response = testRestTemplate.exchange("/api/posts/{id}", HttpMethod.PUT, responseHeader, PostDto.class, 1001);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(response.getBody().getDescription(), "Description");

    }

    @Test
    void whebUpdatePost_then404(){
        PostDto postDto = new PostDto();
        postDto.setId(1001);
        postDto.setDescription("Description");
        postDto.setContent("contents");
        postDto.setTitle("Title");
        postDto.setCategoryId(1000L);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminToken);
        HttpEntity<PostDto> responseHeader = new HttpEntity<>(postDto, headers);
        ResponseEntity<PostDto> response = testRestTemplate.exchange("/api/posts/{id}", HttpMethod.PUT, responseHeader, PostDto.class, 1);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void whenUpdatePost_then400 (){
        PostDto postDto = new PostDto();
        postDto.setId(1001);
        postDto.setDescription("Description");
        postDto.setContent("contents");
        postDto.setCategoryId(1000L);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminToken);
        HttpEntity<PostDto> responseHeader = new HttpEntity<>(postDto, headers);
        ResponseEntity<PostDto> response = testRestTemplate.exchange("/api/posts/{id}", HttpMethod.PUT, responseHeader, PostDto.class, 1);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    
}

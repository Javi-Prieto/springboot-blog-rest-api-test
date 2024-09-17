package com.springboot.blog.user;


import com.springboot.blog.payload.JWTAuthResponse;
import com.springboot.blog.payload.LoginDto;
import com.springboot.blog.payload.RegisterDto;
import com.springboot.blog.security.JwtTokenProvider;
import org.junit.Assert;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;



@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles({"integration-test"})
@Sql(value = "classpath:import-integration.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)

public class AuthIntegrationTest {

    @Autowired
    TestRestTemplate testRestTemplate;

    LoginDto loginDto = new LoginDto();


    @Test
    void validLogin_then200(){
        loginDto.setUsernameOrEmail("ToRechulon");
        loginDto.setPassword("1234");
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<LoginDto> responseHeaders = new HttpEntity<>(loginDto, headers);
        ResponseEntity<JWTAuthResponse> response = testRestTemplate.exchange("/api/auth/login", HttpMethod.POST, responseHeaders, JWTAuthResponse.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void invalidLogin_then500(){
        loginDto.setUsernameOrEmail("Paco");
        loginDto.setPassword("1234");
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<LoginDto> responseHeaders = new HttpEntity<>(loginDto, headers);
        ResponseEntity<JWTAuthResponse> response = testRestTemplate.exchange("/api/auth/login", HttpMethod.POST, responseHeaders, JWTAuthResponse.class);
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode()); //no tiene gestion de errores y da error 500
    }

    @Test
    void validRegistration_then201() {
        RegisterDto registerDto = new RegisterDto();
        registerDto.setName("Nuevo Usuario");
        registerDto.setUsername("nuevoUsuario");
        registerDto.setEmail("nuevoUsuario@example.com");
        registerDto.setPassword("password123");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RegisterDto> request = new HttpEntity<>(registerDto, headers);
        ResponseEntity<String> response = testRestTemplate.postForEntity("/api/auth/register", request, String.class);
        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void invalidRegistration_then400() {
        RegisterDto registerDto = new RegisterDto();
        registerDto.setName("Nuevo Usuario");
        registerDto.setUsername("ToRechulon");
        registerDto.setEmail("nuevoUsuario@example.com");
        registerDto.setPassword("password123");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RegisterDto> request = new HttpEntity<>(registerDto, headers);
        ResponseEntity<String> response = testRestTemplate.postForEntity("/api/auth/register", request, String.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void invalidRegistration_then500() {
        RegisterDto registerDto = new RegisterDto();
        registerDto.setName("Nuevo Usuario");
        registerDto.setEmail("nuevoUsuario@example.com");
        registerDto.setPassword("password123");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RegisterDto> request = new HttpEntity<>(registerDto, headers);
        ResponseEntity<String> response = testRestTemplate.postForEntity("/api/auth/register", request, String.class);
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode()); //no tiene gestion de errores para nulos son obligatorios en la tabla y da error 500
    }
}

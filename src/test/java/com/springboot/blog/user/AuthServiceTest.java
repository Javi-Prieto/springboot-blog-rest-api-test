package com.springboot.blog.user;


import com.springboot.blog.entity.Role;
import com.springboot.blog.exception.BlogAPIException;
import com.springboot.blog.payload.LoginDto;
import com.springboot.blog.payload.RegisterDto;
import com.springboot.blog.repository.RoleRepository;
import com.springboot.blog.repository.UserRepository;
import com.springboot.blog.security.JwtTokenProvider;
import com.springboot.blog.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    RoleRepository roleRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private AuthenticationManager authenticationManager;
    @InjectMocks
    AuthServiceImpl authService;

    RegisterDto registerDto = new RegisterDto("Pedro", "ToReshulon", "email@email.com", "1234");


    @Test
    void Login(){
        LoginDto login = new LoginDto();
        login.setUsernameOrEmail("ToRechulon");
        login.setPassword("1234");
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login.getUsernameOrEmail(), login.getPassword()));

        Mockito.when(authenticationManager.authenticate(Mockito.any())).thenReturn(authentication);
        Mockito.when(jwtTokenProvider.generateToken(Mockito.any())).thenReturn("wasd");

        Assertions.assertEquals("wasd", authService.login(login));

    }

    @Test
    void registerTest(){

        Mockito.when(userRepository.existsByUsername(registerDto.getUsername())).thenReturn(false);
        Mockito.when(userRepository.existsByEmail(registerDto.getEmail())).thenReturn(false);
        Mockito.when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(new Role()));
        Mockito.when(passwordEncoder.encode(registerDto.getPassword())).thenReturn("passowrd");
        String response = authService.register(registerDto);

        Assertions.assertEquals("User registered successfully!.", response);
    }

    @Test
    void registerNotSuccessEmail(){
        Mockito.when(userRepository.existsByEmail(registerDto.getEmail())).thenReturn(true);
        BlogAPIException exception = Assertions.assertThrows(BlogAPIException.class, ()->{
            authService.register(registerDto);
        });

        Assertions.assertEquals("Email is already exists!.", exception.getMessage());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void registerNotSuccessUserName(){
        Mockito.when(userRepository.existsByUsername(registerDto.getUsername())).thenReturn(true);
        BlogAPIException exception = Assertions.assertThrows(BlogAPIException.class, ()->{
            authService.register(registerDto);
        });

        Assertions.assertEquals("Username is already exists!.", exception.getMessage());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }



}

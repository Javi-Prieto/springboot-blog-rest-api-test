package com.springboot.blog.user;

import com.springboot.blog.entity.User;
import com.springboot.blog.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Optional;

@DataJpaTest
@ActiveProfiles({"test"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@Sql(value = "classpath:insert-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer(DockerImageName.parse("postgres:16-alpine"))
            .withUsername("testUser")
            .withPassword("testSecret")
            .withDatabaseName("testDatabase");

    @Test
    void findByUsername() {
        Optional<User> usuario = userRepository.findByUsername("ToRechulon");
        Assertions.assertEquals("ToRechulon", usuario.get().getUsername());

    }

    @Test
    void findByEmail(){
        Optional<User> usuario = userRepository.findByEmail("pedro@gmail.com");
        Assertions.assertEquals("pedro@gmail.com", usuario.get().getEmail());
    }

    @Test
    void  findByUsernameOrEmail(){
        Optional<User> usuario = userRepository.findByEmail("pedro@gmail.com");
        Assertions.assertEquals("pedro@gmail.com", usuario.get().getEmail());
        usuario = userRepository.findByUsername("ToRechulon");
        Assertions.assertEquals("ToRechulon", usuario.get().getUsername());
    }

    @Test
    void existsByUsername(){
        boolean respuesta = userRepository.existsByUsername("ToRechulon");
        Assertions.assertTrue(respuesta);
    }

    @Test
    void existsByEmail(){
        boolean respuesta = userRepository.existsByEmail("pedro@gmail.com");
        Assertions.assertTrue(respuesta);
    }
}

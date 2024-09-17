package com.springboot.blog.Role;

import com.springboot.blog.entity.Role;
import com.springboot.blog.repository.RoleRepository;
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

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@ActiveProfiles({"test"})
@Sql(value = "classpath:import-role-repository-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class RoleRepositoryTest {

    @Autowired
    RoleRepository repository;

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer(DockerImageName.parse("postgres:16-alpine"))
            .withUsername("postgres")
            .withPassword("12345678")
            .withDatabaseName("myblog");

    @Test
    void findByName() {

        Optional<Role> result = repository.findByName("ADMIN-TEST");

        if(Objects.equals(result.get().getName(), "ADMIN-TEST")){
            assertEquals(1, result.get().getId());
            assertNotNull(result);
        }else{
            assertNotEquals(2, result.get().getId());
        }


    }
}
package com.springboot.blog.Post;

import com.springboot.blog.entity.Category;
import com.springboot.blog.entity.Post;
import com.springboot.blog.repository.PostRepository;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
import org.testcontainers.utility.MountableFile;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@DataJpaTest
@ActiveProfiles({"test"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(value = {"classpath:import-test-post.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class PostRepositoryTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer(DockerImageName.parse("postgres:16.0"))
            .withUsername("blog")
            .withPassword("blog")
            .withDatabaseName("postgres-blog");


    @Autowired
    PostRepository repository;

    @ParameterizedTest
    @CsvSource({"1", "2"})
    void findByCategoryIdTest(Long categoryId){
        List<Post> postList = List.of(new Post(1L,
                "hola",
                "hola mundo java",
                "System.out.println(Hola Mundo)",
                Set.of(), new Category()));
        if(categoryId == 1) {
            assertEquals(1, repository.findByCategoryId(categoryId).size());
            assertEquals(postList.get(0).getDescription(), repository.findByCategoryId(categoryId).get(0).getDescription());
        }else {
            assertEquals(0, repository.findByCategoryId(categoryId).size());
        }
    }
}

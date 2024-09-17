package com.springboot.blog.Comment;

import com.springboot.blog.entity.Category;
import com.springboot.blog.entity.Comment;
import com.springboot.blog.entity.Post;
import com.springboot.blog.repository.CommentRepository;
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

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@DataJpaTest
@ActiveProfiles({"test"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(value = {"classpath:import-test-comment.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class CommentRepoTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer(DockerImageName.parse("postgres:16.0"))
            .withUsername("blog")
            .withPassword("blog")
            .withDatabaseName("postgres-blog");
    @Autowired
    CommentRepository repository;

    @ParameterizedTest
    @CsvSource({"3","2"})
    void testFindByPostId (Long postId) throws Exception{

        List<Comment> comments = List.of(new Comment(1L,"hola","description","contenmnnnnnn", new Post()));
        if (postId == 1 ){
            assertEquals(1,repository.findByPostId(postId).size());
            assertEquals(comments.get(0).getBody(),repository.findByPostId(postId).get(0).getBody());
        }else {
            assertEquals(0,repository.findByPostId(postId).size());
        }
    }

}

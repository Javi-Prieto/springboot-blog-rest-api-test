package com.springboot.blog.Comment;

import com.springboot.blog.entity.Comment;
import com.springboot.blog.entity.Post;
import com.springboot.blog.payload.CommentDto;
import com.springboot.blog.repository.CommentRepository;
import com.springboot.blog.repository.PostRepository;
import com.springboot.blog.service.impl.CommentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    private final ModelMapper mapper = new ModelMapper();

    @BeforeEach
    void setUp() {
        commentService.setMapper(mapper);
    }
//
    @Test
    void createCommentTest() {

        long postId = 1L;
        CommentDto commentDto = new CommentDto();
        commentDto.setName("nombre del comentario");
        commentDto.setEmail("email del comment");
        commentDto.setBody("cuerpo del comment");

        Post post = new Post();
        post.setId(postId);

        Comment comment = new Comment();
        comment.setName(commentDto.getName());
        comment.setEmail(commentDto.getEmail());
        comment.setBody(commentDto.getBody());
        comment.setPost(post);

        Mockito.when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        Mockito.when(commentRepository.save(Mockito.any(Comment.class))).thenReturn(comment);


        CommentDto result = commentService.createComment(postId, commentDto);


        assertEquals(commentDto.getName(), result.getName());
        assertEquals(commentDto.getEmail(), result.getEmail());
        assertEquals(commentDto.getBody(), result.getBody());
    }


    @Test
    void getCommentsByPostIdTest() {

        long postId = 1L;
        Comment comment1 = new Comment();
        comment1.setId(1L);
        comment1.setName("nombre del comentario 1");
        comment1.setEmail("email1@example.com");
        comment1.setBody("cuerpo del comentario 1");

        Comment comment2 = new Comment();
        comment2.setId(2L);
        comment2.setName("nombre del comentario 2");
        comment2.setEmail("email2@example.com");
        comment2.setBody("cuerpo del comentario 2");

        List<Comment> comments = List.of(comment1, comment2);


        Mockito.when(commentRepository.findByPostId(postId)).thenReturn(comments);


        List<CommentDto> result = commentService.getCommentsByPostId(postId);

        assertEquals(comments.size(), result.size());
        for (int i = 0; i < comments.size(); i++) {
            Comment comment = comments.get(i);
            CommentDto commentDto = result.get(i);

            assertEquals(comment.getName(), commentDto.getName());
            assertEquals(comment.getEmail(), commentDto.getEmail());
            assertEquals(comment.getBody(), commentDto.getBody());
        }
    }


    @Test
    void getCommentByIdTest() {

        long postId = 1L;
        long commentId = 1L;
        Post post = new Post();
        post.setId(postId);

        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setName("nombre del comentario");
        comment.setEmail("email del comment");
        comment.setBody("cuerpo del comment");
        comment.setPost(post);


        Mockito.when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        Mockito.when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        CommentDto result = commentService.getCommentById(postId, commentId);


        assertEquals(comment.getName(), result.getName());
        assertEquals(comment.getEmail(), result.getEmail());
        assertEquals(comment.getBody(), result.getBody());
    }


    @Test
    void updateCommentTest() {

        long postId = 1L;
        long commentId = 1L;
        Post post = new Post();
        post.setId(postId);

        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setName("nombre del comentario");
        comment.setEmail("email del comment");
        comment.setBody("cuerpo del comment");
        comment.setPost(post);

        CommentDto commentRequest = new CommentDto();
        commentRequest.setName("nuevo nombre del comentario");
        commentRequest.setEmail("nuevo email del comment");
        commentRequest.setBody("nuevo cuerpo del comment");

        Comment updatedComment = new Comment();
        updatedComment.setId(commentId);
        updatedComment.setName(commentRequest.getName());
        updatedComment.setEmail(commentRequest.getEmail());
        updatedComment.setBody(commentRequest.getBody());
        updatedComment.setPost(post);

        Mockito.when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        Mockito.when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        Mockito.when(commentRepository.save(Mockito.any(Comment.class))).thenReturn(updatedComment);

        CommentDto result = commentService.updateComment(postId, commentId, commentRequest);


        assertEquals(commentRequest.getName(), result.getName());
        assertEquals(commentRequest.getEmail(), result.getEmail());
        assertEquals(commentRequest.getBody(), result.getBody());
    }

    @Test
    void deleteCommentTest() {

        long postId = 1L;
        long commentId = 1L;
        Post post = new Post();
        post.setId(postId);

        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setName("nombre del comentario");
        comment.setEmail("email del comment");
        comment.setBody("cuerpo del comment");
        comment.setPost(post);


        Mockito.when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        Mockito.when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));


        commentService.deleteComment(postId, commentId);
        
        Mockito.verify(commentRepository, Mockito.times(1)).delete(comment);
    }



}






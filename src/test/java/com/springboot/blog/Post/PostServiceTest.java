package com.springboot.blog.Post;

import com.springboot.blog.entity.Category;
import com.springboot.blog.entity.Post;
import com.springboot.blog.payload.PostDto;
import com.springboot.blog.payload.PostResponse;
import com.springboot.blog.repository.CategoryRepository;
import com.springboot.blog.repository.PostRepository;
import com.springboot.blog.service.impl.PostServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {


    @Mock
    PostRepository repository;

    @Mock
    CategoryRepository categoryRepository;

    ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    PostServiceImpl service = new PostServiceImpl(repository, modelMapper, categoryRepository);

    public static Stream<Arguments> getData() {
        PostDto toReturn = new PostDto();
        toReturn.setId(1L);
        toReturn.setTitle("title");
        toReturn.setCategoryId(1L);
        return  Stream.of(
                Arguments.of(toReturn),
                Arguments.of((Object) null)
        );
    }

    @ParameterizedTest
    @MethodSource({"getData"})
    void createPostTest(PostDto postDto){
        Post post = modelMapper.map(postDto, Post.class);
        Category category = new Category(1L, "Gym", "Something related to gym", List.of());
        Mockito.when(categoryRepository.findById(postDto.getCategoryId())).thenReturn(Optional.of(category));
        Mockito.when(repository.save(any(Post.class))).thenReturn(post);

        assertEquals(category.getId(), service.createPost(postDto).getId());
        assertTrue(category.getPosts().contains(modelMapper.map(service.createPost(postDto), Post.class)));
    }

    @Test
    void getAllPostTest(){
        Pageable pageable = PageRequest.of(1, 2, Sort.by("title").ascending());
        Post post = new Post(1L, "title", "description", "", Set.of(), new Category());
        PostResponse response = new PostResponse();
        response.setContent(List.of(modelMapper.map(post, PostDto.class)));
        Mockito.when(repository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(
                post
                )));
        assertEquals(response.getContent(), service.getAllPosts(1, 2, "title", "ASC").getContent());
    }

    public static Stream<Arguments> getMoreData() {
        PostDto toReturn = new PostDto();
        toReturn.setId(1L);
        toReturn.setTitle("title");
        toReturn.setDescription("description");
        toReturn.setContent("content");
        toReturn.setCategoryId(1L);
        return  Stream.of(
                Arguments.of(toReturn, 1L),
                Arguments.of((Object) null)
        );
    }

    @ParameterizedTest
    @MethodSource({"getMoreData"})
    void updatePostTest(PostDto postDto, long id){
        Category toUpdateC = new Category(2L, "name", "description", List.of());
        Post toUpdate = new Post(1L, "not title", "not description", "not content", Set.of(), toUpdateC);
        toUpdateC.setPosts(List.of(toUpdate));
        Category toCheck = new Category(1L, "name", "description", List.of());
        Mockito.when(repository.findById(any(Long.class))).thenReturn(Optional.of(toUpdate));
        Mockito.when(categoryRepository.findById(any(Long.class))).thenReturn(Optional.of(toCheck));
        Mockito.when(repository.save(any(Post.class))).thenReturn(modelMapper.map(postDto, Post.class));

        assertEquals(postDto.getTitle(), service.updatePost(postDto, id).getTitle());
        assertEquals(toCheck.getId(), service.updatePost(postDto, id).getCategoryId());
        assertFalse(toUpdateC.getPosts().stream().map(Post::getId).toList().contains(service.updatePost(postDto, id).getId()));
    }

    @Test
    void deletePostByIdTest(){
        Category toCheck = new Category(1L, "name", "description", List.of());
        Post toDelete = new Post(1L, "not title", "not description", "not content", Set.of(), toCheck);
        toCheck.setPosts(List.of(toDelete));
        Mockito.when(repository.findById(any(Long.class))).thenReturn(Optional.of(toDelete));
        service.deletePostById(1L);
        verify(repository, times(1)).delete(any(Post.class));
        assertFalse(toCheck.getPosts().stream().map(Post::getId).toList().contains(toDelete.getId()));
    }

}

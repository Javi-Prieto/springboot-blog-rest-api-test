package com.springboot.blog.Category;

import com.springboot.blog.entity.Category;
import com.springboot.blog.exception.ResourceNotFoundException;
import com.springboot.blog.payload.CategoryDto;
import com.springboot.blog.repository.CategoryRepository;
import com.springboot.blog.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    CategoryRepository repository;

    @InjectMocks
    CategoryServiceImpl service;

    @Mock
    ModelMapper modelMapper;

    @Test
    void addCategory() {
        CategoryDto dto = new CategoryDto(1L,"Aventura","Categoria de posts de aventura");
        Category category = new Category(1L,"Aventura","Categoria de posts de aventura", Collections.emptyList());

        when(modelMapper.map(dto, Category.class)).thenReturn(category);
        when(repository.save(category)).thenReturn(category);
        when(modelMapper.map(category, CategoryDto.class)).thenReturn(dto);

        CategoryDto result = service.addCategory(dto);

        assertEquals(result.getName(), dto.getName());
        assertEquals(result.getId(), dto.getId());
        Assertions.assertNotNull(result);

    }

    @Test
    void getCategory() {
        Category category = new Category(1L,"Accion","Posts de accion",Collections.emptyList());
        CategoryDto dto = new CategoryDto(1L,"Accion","Posts de accion");

        when(repository.findById(category.getId())).thenReturn(Optional.of(category));
        when(modelMapper.map(category,CategoryDto.class)).thenReturn(dto);

        CategoryDto result = service.getCategory(category.getId());

        assertEquals(result.getDescription(),"Posts de accion");
        assertEquals(result.getName(),"Accion");
        Assertions.assertNotEquals(2,result.getId());

    }

    @Test
    void getCategory_WhenCategoryNotFound() {
        Category category = new Category();
        category.setId(1L);

        when(repository.findById(category.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getCategory(category.getId()));

    }

    @Test
    void getAllCategories() {

        Category c1 = new Category(1L,"Drama","Posts de drama", Collections.emptyList());
        Category c2 = new Category(2L,"Misterio","Posts de misterio", Collections.emptyList());
        Category c3 = new Category(3L,"Comedia","Posts de comedia", Collections.emptyList());

        List<Category> data = List.of(c1,c2,c3);

        CategoryDto dto1 = new CategoryDto(1L,"Drama","Posts de drama");
        CategoryDto dto2 = new CategoryDto(2L,"Misterio","Posts de misterio");
        CategoryDto dto3 = new CategoryDto(3L,"Comedia","Posts de comedia");

        List<CategoryDto> datadto = List.of(dto1,dto2,dto3);

        when(repository.findAll()).thenReturn(data);
        when(modelMapper.map(Mockito.any(Category.class), Mockito.eq(CategoryDto.class)))
                .thenReturn(dto1, dto2, dto3);

        List<CategoryDto> result = service.getAllCategories();

        Assertions.assertEquals(datadto, result);
        Assertions.assertEquals(3, result.size());
        Assertions.assertNotNull(result);
    }

    @Test
    void updateCategory() {
        Category c1 = new Category(1L,"Drama","Posts de drama", Collections.emptyList());
        Category c1Updated = new Category(1L, "Drama editado", "Descripcion editada", Collections.emptyList());
        CategoryDto dto = new CategoryDto(1L, "Drama editado", "Descripcion editada");


        when(repository.findById(c1.getId())).thenReturn(Optional.of(c1));
        when(repository.save(c1)).thenReturn(c1Updated);
        when(modelMapper.map(c1Updated,CategoryDto.class)).thenReturn(dto);

        CategoryDto result = service.updateCategory(dto, 1L);

        Assertions.assertEquals(dto, result);
        Assertions.assertEquals("Drama editado", result.getName());
        Assertions.assertNotEquals("Posts de drama",result.getDescription());
        Assertions.assertNotNull(result);
    }

    @Test
    void updateCategory_whenCategoryIdNotFound() {
        Category c1 = new Category(1L,"Drama","Posts de drama", Collections.emptyList());
        CategoryDto dto = new CategoryDto(1L, "Drama editado", "Descripcion editada");


        when(repository.findById(c1.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.updateCategory(dto, c1.getId()));

    }


    @Test
    void deleteCategory() {
        Category c1 = new Category(1L,"Drama","Posts de drama", Collections.emptyList());

        when(repository.findById(c1.getId())).thenReturn(Optional.of(c1));

        service.deleteCategory(c1.getId());

        verify(repository, times(1)).delete(c1);

    }

    @Test
    void deleteCategory_whenCategoryIdNotFound() {
        Category c1 = new Category(1L,"Drama","Posts de drama", Collections.emptyList());

        when(repository.findById(c1.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.deleteCategory(c1.getId()));

    }
}
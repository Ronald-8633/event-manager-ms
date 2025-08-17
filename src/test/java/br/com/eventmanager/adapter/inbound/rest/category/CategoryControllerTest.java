package br.com.eventmanager.adapter.inbound.rest.category;

import br.com.eventmanager.adapter.outbound.persistence.CategoryRepository;
import br.com.eventmanager.domain.Category;
import br.com.eventmanager.domain.dto.CategoryDTO;
import br.com.eventmanager.domain.mapper.EventMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class CategoryControllerTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private EventMapper eventMapper;

    @InjectMocks
    private CategoryController categoryController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllCategories() {
        Category cat1 = Category.builder().name("Cat 1").build();
        Category cat2 = Category.builder().name("Cat 2").build();

        when(categoryRepository.findByIsActiveTrue()).thenReturn(Arrays.asList(cat1, cat2));
        when(eventMapper.toCategoryDTO(cat1)).thenReturn(CategoryDTO.builder().name("Cat 1").build());
        when(eventMapper.toCategoryDTO(cat2)).thenReturn(CategoryDTO.builder().name("Cat 2").build());

        ResponseEntity<List<CategoryDTO>> response = categoryController.getAllCategories();

        assertNotNull(response);
        assertEquals(2, response.getBody().size());
        verify(categoryRepository, times(1)).findByIsActiveTrue();
    }

    @Test
    void testGetCategoryById_Found() {
        Category category = Category.builder().id("1").name("Category 1").build();

        when(categoryRepository.findById("1")).thenReturn(Optional.of(category));
        when(eventMapper.toCategoryDTO(category)).thenReturn(CategoryDTO.builder().name("Category 1").build());

        ResponseEntity<CategoryDTO> response = categoryController.getCategoryById("1");

        assertNotNull(response);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals("Category 1", response.getBody().getName());
        verify(categoryRepository, times(1)).findById("1");
    }

    @Test
    void testGetCategoryById_NotFound() {
        when(categoryRepository.findById("1")).thenReturn(Optional.empty());

        ResponseEntity<CategoryDTO> response = categoryController.getCategoryById("1");

        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
        verify(categoryRepository, times(1)).findById("1");
    }

    @Test
    void testGetCategoryByName_Found() {
        Category category = Category.builder().id("1").name("Category A").build();

        when(categoryRepository.findByName("Category A")).thenReturn(Optional.of(category));
        when(eventMapper.toCategoryDTO(category)).thenReturn(CategoryDTO.builder().name("Category A").build());

        ResponseEntity<CategoryDTO> response = categoryController.getCategoryByName("Category A");

        assertNotNull(response);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals("Category A", response.getBody().getName());
        verify(categoryRepository, times(1)).findByName("Category A");
    }

    @Test
    void testGetCategoryByName_NotFound() {
        when(categoryRepository.findByName("Unknown")).thenReturn(Optional.empty());

        ResponseEntity<CategoryDTO> response = categoryController.getCategoryByName("Unknown");

        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
        verify(categoryRepository, times(1)).findByName("Unknown");
    }
}


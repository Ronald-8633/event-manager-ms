package br.com.eventmanager.adapter.inbound;

import br.com.eventmanager.adapter.outbound.persistence.CategoryRepository;
import br.com.eventmanager.domain.Category;
import br.com.eventmanager.domain.dto.CategoryDTO;
import br.com.eventmanager.domain.mapper.EventMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Category management APIs")
public class CategoryController {
    
    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;
    
    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        List<Category> categories = categoryRepository.findByIsActiveTrue();
        List<CategoryDTO> categoryDTOs = categories.stream()
                .map(eventMapper::toCategoryDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(categoryDTOs);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable String id) {
        return categoryRepository.findById(id)
                .map(eventMapper::toCategoryDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/name/{name}")
    public ResponseEntity<CategoryDTO> getCategoryByName(@PathVariable String name) {
        return categoryRepository.findByName(name)
                .map(eventMapper::toCategoryDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

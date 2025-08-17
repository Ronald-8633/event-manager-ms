package br.com.eventmanager.adapter.inbound.rest.category;

import br.com.eventmanager.adapter.outbound.persistence.CategoryRepository;
import br.com.eventmanager.domain.Category;
import br.com.eventmanager.domain.dto.CategoryDTO;
import br.com.eventmanager.domain.mapper.EventMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class CategoryController implements CategoryApi {
    
    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;
    
   @Override
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        List<Category> categories = categoryRepository.findByIsActiveTrue();
        List<CategoryDTO> categoryDTOs = categories.stream()
                .map(eventMapper::toCategoryDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(categoryDTOs);
    }

    @Override
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable String id) {
        return categoryRepository.findById(id)
                .map(eventMapper::toCategoryDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<CategoryDTO> getCategoryByName(@PathVariable String name) {
        return categoryRepository.findByName(name)
                .map(eventMapper::toCategoryDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

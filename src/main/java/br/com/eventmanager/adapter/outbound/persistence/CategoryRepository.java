package br.com.eventmanager.adapter.outbound.persistence;

import br.com.eventmanager.domain.Category;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends MongoRepository<Category, String> {
    
    Optional<Category> findByName(String name);

    Optional<Category> findByCategoryCode(String name);

    List<Category> findByIsActiveTrue();
    
    List<Category> findByIsActive(Boolean isActive);
}

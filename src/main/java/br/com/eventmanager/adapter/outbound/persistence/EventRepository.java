package br.com.eventmanager.adapter.outbound.persistence;

import br.com.eventmanager.domain.Event;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends MongoRepository<Event, String> {
    
    List<Event> findByCategoryCode(String category);
    
    List<Event> findByStatus(Event.EventStatus status);
    
    List<Event> findByOrganizerId(String organizerId);
    
    @Query("{'title': {$regex: ?0, $options: 'i'}}")
    List<Event> findByTitleContainingIgnoreCase(String title);
    
    @Query("{'tags': {$in: ?0}}")
    List<Event> findByTagsIn(List<String> tags);
}

package br.com.eventmanager.adapter.outbound.persistence;

import br.com.eventmanager.domain.Event;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends MongoRepository<Event, String> {
    
    List<Event> findByCategoryCode(String category);
    
    List<Event> findByStatus(Event.EventStatus status);
    
    List<Event> findByOrganizerId(String organizerId);
}

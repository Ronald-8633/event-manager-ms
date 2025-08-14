package br.com.eventmanager.application.service;

import br.com.eventmanager.adapter.outbound.persistence.CategoryRepository;
import br.com.eventmanager.adapter.outbound.persistence.EventRepository;
import br.com.eventmanager.adapter.outbound.persistence.LocationRepository;
import br.com.eventmanager.domain.Category;
import br.com.eventmanager.domain.Event;
import br.com.eventmanager.domain.Location;
import br.com.eventmanager.domain.dto.CategoryDTO;
import br.com.eventmanager.domain.dto.EventDTO;
import br.com.eventmanager.domain.dto.EventRequestDTO;
import br.com.eventmanager.domain.dto.LocationDTO;
import br.com.eventmanager.domain.mapper.EventMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {
    
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final EventMapper eventMapper;
    
    public Event createEvent(Event event) {
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());
        event.setStatus(Event.EventStatus.DRAFT);
        event.setCurrentCapacity(0);
        
        log.info("Creating new event: {}", event.getTitle());
        return eventRepository.save(event);
    }
    
    public Optional<Event> findById(String id) {
        return eventRepository.findById(id);
    }
    
    public Optional<EventDTO> findEventDTOById(String id) {
        return eventRepository.findById(id)
                .map(event -> {
                    Category category = categoryRepository.findByCategoryCode(event.getCategoryCode()).orElse(null);
                    Location location = locationRepository.findByLocationCode(event.getLocationCode()).orElse(null);
                    return buildEventDTO(event, category, location);
                });
    }
    
    public List<Event> findAll() {
        return eventRepository.findAll();
    }
    
    public List<EventDTO> findAllEventDTOs() {
        return eventRepository.findAll().stream()
                .map(event -> {
                    Category category = categoryRepository.findByCategoryCode(event.getCategoryCode()).orElse(null);
                    Location location = locationRepository.findByLocationCode(event.getLocationCode()).orElse(null);
                    return buildEventDTO(event, category, location);
                })
                .collect(Collectors.toList());
    }
    
    public List<Event> findByCategoryCode(String category) {
        return eventRepository.findByCategoryCode(category);
    }
    
    public List<EventDTO> findEventDTOsByCategory(String category) {
        return eventRepository.findByCategoryCode(category).stream()
                .map(event -> {
                    Category categoryEntity = categoryRepository.findById(event.getCategoryCode()).orElse(null);
                    Location location = locationRepository.findById(event.getLocationCode()).orElse(null);
                    return buildEventDTO(event, categoryEntity, location);
                })
                .collect(Collectors.toList());
    }
    
    public List<Event> findByStatus(Event.EventStatus status) {
        return eventRepository.findByStatus(status);
    }
    
    public List<EventDTO> findEventDTOsByStatus(Event.EventStatus status) {
        return eventRepository.findByStatus(status).stream()
                .map(event -> {
                    Category category = categoryRepository.findById(event.getCategoryCode()).orElse(null);
                    return buildEventDTO(event, category, null);
                })
                .collect(Collectors.toList());
    }
    
    public Event updateEvent(String id, EventRequestDTO eventDetails) {
        return eventRepository.findById(id)
                .map(existingEvent -> {
                    eventMapper.toEvent(eventDetails, existingEvent);
                    log.info("Updating event: {}", id);
                    return eventRepository.save(existingEvent);
                })
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));
    }
    
    public void deleteEvent(String id) {
        log.info("Deleting event: {}", id);
        eventRepository.deleteById(id);
    }
    
    public Event publishEvent(String id) {
        return eventRepository.findById(id)
                .map(event -> {
                    event.setStatus(Event.EventStatus.PUBLISHED);
                    event.setUpdatedAt(LocalDateTime.now());
                    log.info("Publishing event: {}", id);
                    return eventRepository.save(event);
                })
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));
    }
    
    public boolean addAttendee(String eventId, String userId) {
        return eventRepository.findById(eventId)
                .map(event -> {
                    if (event.getCurrentCapacity() < event.getMaxCapacity()) {
                        addAttendee(userId, event);
                        event.setCurrentCapacity(event.getCurrentCapacity() + 1);
                        event.setUpdatedAt(LocalDateTime.now());
                        eventRepository.save(event);
                        log.info("Added attendee {} to event {}", userId, eventId);
                        return true;
                    }
                    log.warn("Event {} is at full capacity", eventId);
                    return false;
                })
                .orElse(false);
    }

    private void addAttendee(String userId, Event event) {
        if(event.getAttendees() == null ) {
            event.setAttendees(HashSet.newHashSet(0));
        }
        event.getAttendees().add(userId);
    }

    private EventDTO buildEventDTO(Event event, Category category, Location location) {
        CategoryDTO categoryDTO = category != null ? eventMapper.toCategoryDTO(category) : null;
        LocationDTO locationDTO = location != null ? eventMapper.toLocationDTO(location) : null;
        
        return EventDTO.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .categoryCode(categoryDTO)
                .locationCode(locationDTO)
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .maxCapacity(event.getMaxCapacity())
                .currentCapacity(event.getCurrentCapacity())
                .price(event.getPrice())
                .organizerId(event.getOrganizerId())
                .status(event.getStatus())
                .tags(event.getTags())
                .attendees(event.getAttendees())
                .imageUrl(event.getImageUrl())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .build();
    }
}

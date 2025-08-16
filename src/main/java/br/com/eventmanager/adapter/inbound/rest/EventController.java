package br.com.eventmanager.adapter.inbound.rest;

import br.com.eventmanager.application.service.EventService;
import br.com.eventmanager.domain.Event;
import br.com.eventmanager.domain.dto.AttendeeResponseDTO;
import br.com.eventmanager.domain.dto.EventDTO;
import br.com.eventmanager.domain.dto.EventRequestDTO;
import br.com.eventmanager.domain.dto.ResultDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
@Tag(name = "Events", description = "Event management APIs")
public class EventController {
    
    private final EventService eventService;
    
    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody @Valid EventRequestDTO event) {
        Event createdEvent = eventService.createEvent(event);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> getEventById(@PathVariable String id) {
        return eventService.findEventDTOById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    public ResponseEntity<List<EventDTO>> getAllEvents() {
        List<EventDTO> events = eventService.findAllEventDTOs();
        return ResponseEntity.ok(events);
    }
    
    @GetMapping("/category/{category}")
    public ResponseEntity<List<EventDTO>> getEventsByCategory(@PathVariable String category) {
        List<EventDTO> events = eventService.findEventDTOsByCategory(category);
        return ResponseEntity.ok(events);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<EventDTO>> getEventsByStatus(@PathVariable Event.EventStatus status) {
        List<EventDTO> events = eventService.findEventDTOsByStatus(status);
        return ResponseEntity.ok(events);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable String id, @RequestBody EventRequestDTO eventDetails) {
        Event updatedEvent = eventService.updateEvent(id, eventDetails);
        return ResponseEntity.ok(updatedEvent);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable String id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<Event> publishEvent(@PathVariable String id) {

        Event publishedEvent = eventService.publishEvent(id);

        return ResponseEntity.ok(publishedEvent);
    }

    @PostMapping("/{id}/attendees/{userId}")
    public ResponseEntity<AttendeeResponseDTO> addAttendee(@PathVariable String id, @PathVariable String userId) {
        ResultDTO<AttendeeResponseDTO> resultDTO = eventService.addAttendee(id, userId);

        if (resultDTO.isSuccess()) {
            return ResponseEntity.ok(resultDTO.getData());
        } else {
            return ResponseEntity.badRequest()
                    .body(AttendeeResponseDTO.builder()
                            .eventId(id)
                            .userId(userId)
                            .success(false)
                            .message(resultDTO.getError())
                            .build());
        }
    }
}

package br.com.eventmanager.adapter.inbound.rest.event;

import br.com.eventmanager.application.service.EventService;
import br.com.eventmanager.domain.Event;
import br.com.eventmanager.domain.dto.AttendeeResponseDTO;
import br.com.eventmanager.domain.dto.EventDTO;
import br.com.eventmanager.domain.dto.EventRequestDTO;
import br.com.eventmanager.domain.dto.ResultDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class EventController implements EventApi {
    
    private final EventService eventService;
    
   @Override
    public ResponseEntity<Event> createEvent(EventRequestDTO event) {
        Event createdEvent = eventService.createEvent(event);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
    }
    
    @Override
    public ResponseEntity<EventDTO> getEventById(String id) {
        return eventService.findEventDTOById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Override
    public ResponseEntity<List<EventDTO>> getAllEvents() {
        List<EventDTO> events = eventService.findAllEventDTOs();
        return ResponseEntity.ok(events);
    }
    
    @Override
    public ResponseEntity<List<EventDTO>> getEventsByCategory(String category) {
        List<EventDTO> events = eventService.findEventDTOsByCategory(category);
        return ResponseEntity.ok(events);
    }
    
    @Override
    public ResponseEntity<List<EventDTO>> getEventsByStatus(Event.EventStatus status) {
        List<EventDTO> events = eventService.findEventDTOsByStatus(status);
        return ResponseEntity.ok(events);
    }

    @Override
    public ResponseEntity<Event> updateEvent(String id,EventRequestDTO eventDetails) {
        Event updatedEvent = eventService.updateEvent(id, eventDetails);
        return ResponseEntity.ok(updatedEvent);
    }
    
    @Override
    public ResponseEntity<Void> deleteEvent(String id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Event> publishEvent(@PathVariable String id) {

        Event publishedEvent = eventService.publishEvent(id);

        return ResponseEntity.ok(publishedEvent);
    }

    @Override
    public ResponseEntity<AttendeeResponseDTO> addAttendee(String id,String userId) {
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

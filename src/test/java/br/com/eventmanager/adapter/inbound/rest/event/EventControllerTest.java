package br.com.eventmanager.adapter.inbound.rest.event;

import br.com.eventmanager.application.service.EventService;
import br.com.eventmanager.application.service.EventSuggestionService;
import br.com.eventmanager.domain.Event;
import br.com.eventmanager.domain.dto.AttendeeResponseDTO;
import br.com.eventmanager.domain.dto.EventDTO;
import br.com.eventmanager.domain.dto.EventRequestDTO;
import br.com.eventmanager.domain.dto.ResultDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventControllerTest {

    @Mock
    private EventService eventService;

    @Mock
    private EventSuggestionService suggestionService;

    @InjectMocks
    private EventController controller;

    private Event mockEvent;
    private EventDTO mockEventDTO;
    private EventRequestDTO mockRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockEvent = Event.builder()
                .id("1")
                .title("Evento Teste")
                .status(Event.EventStatus.DRAFT)
                .build();

        mockEventDTO = EventDTO.builder()
                .id("1")
                .title("Evento Teste")
                .status(Event.EventStatus.DRAFT)
                .build();

        mockRequest = EventRequestDTO.builder()
                .title("Evento Teste")
                .build();
    }

    @Test
    void testCreateEvent() {
        when(eventService.createEvent(mockRequest)).thenReturn(mockEvent);

        ResponseEntity<Event> response = controller.createEvent(mockRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(mockEvent, response.getBody());
        verify(eventService, times(1)).createEvent(mockRequest);
    }

    @Test
    void testGetEventById_Found() {
        when(eventService.findEventDTOById("1")).thenReturn(Optional.of(mockEventDTO));

        ResponseEntity<EventDTO> response = controller.getEventById("1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockEventDTO, response.getBody());
        verify(eventService, times(1)).findEventDTOById("1");
    }

    @Test
    void testGetEventById_NotFound() {
        when(eventService.findEventDTOById("1")).thenReturn(Optional.empty());

        ResponseEntity<EventDTO> response = controller.getEventById("1");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(eventService, times(1)).findEventDTOById("1");
    }

    @Test
    void testGetAllEvents() {
        when(eventService.findAllEventDTOs()).thenReturn(List.of(mockEventDTO));

        ResponseEntity<List<EventDTO>> response = controller.getAllEvents();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(eventService, times(1)).findAllEventDTOs();
    }

    @Test
    void testUpdateEvent() {
        when(eventService.updateEvent("1", mockRequest)).thenReturn(mockEvent);

        ResponseEntity<Event> response = controller.updateEvent("1", mockRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockEvent, response.getBody());
        verify(eventService, times(1)).updateEvent("1", mockRequest);
    }

    @Test
    void testDeleteEvent() {
        doNothing().when(eventService).deleteEvent("1");

        ResponseEntity<Void> response = controller.deleteEvent("1");

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(eventService, times(1)).deleteEvent("1");
    }

    @Test
    void testPublishEvent() {
        when(eventService.publishEvent("1")).thenReturn(mockEvent);

        ResponseEntity<Event> response = controller.publishEvent("1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockEvent, response.getBody());
        verify(eventService, times(1)).publishEvent("1");
    }

    @Test
    void testAddAttendee_Success() {
        AttendeeResponseDTO attendeeResponse = AttendeeResponseDTO.builder()
                .eventId("1")
                .userId("user1")
                .success(true)
                .message("Usuário inscrito")
                .build();

        when(eventService.addAttendee("1", "user1"))
                .thenReturn(ResultDTO.success(attendeeResponse));

        ResponseEntity<AttendeeResponseDTO> response = controller.addAttendee("1", "user1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        verify(eventService, times(1)).addAttendee("1", "user1");
    }

    @Test
    void testAddAttendee_Failure() {
        when(eventService.addAttendee("1", "user1"))
                .thenReturn(ResultDTO.failure("Evento cheio"));

        ResponseEntity<AttendeeResponseDTO> response = controller.addAttendee("1", "user1");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Evento cheio", response.getBody().getMessage());
        verify(eventService, times(1)).addAttendee("1", "user1");
    }

    @Test
    void testGetSuggestions() {
        when(suggestionService.suggestEventsForUser("user1"))
                .thenReturn("Sugestão de evento");

        ResponseEntity<String> response = controller.getSuggestions("user1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Sugestão de evento", response.getBody());
        verify(suggestionService, times(1)).suggestEventsForUser("user1");
    }
}
package br.com.eventmanager.application.service;

import br.com.eventmanager.adapter.inbound.rest.exception.BusinessException;
import br.com.eventmanager.adapter.outbound.persistence.CategoryRepository;
import br.com.eventmanager.adapter.outbound.persistence.EventRepository;
import br.com.eventmanager.adapter.outbound.persistence.LocationRepository;
import br.com.eventmanager.domain.Category;
import br.com.eventmanager.domain.Event;
import br.com.eventmanager.domain.Location;
import br.com.eventmanager.domain.User;
import br.com.eventmanager.domain.dto.EventDTO;
import br.com.eventmanager.domain.dto.EventRequestDTO;
import br.com.eventmanager.domain.mapper.EventMapper;
import br.com.eventmanager.domain.security.Permission;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EventService Tests")
@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private EventMapper eventMapper;

    @Mock
    private MessageService messageService;

    @Mock
    private PublishingValidationService publishingValidationService;

    @Mock
    private AttendeeValidationChainService attendeeValidationChainService;

    @Mock
    private PermissionAuthorizationService permissionAuthorizationService;

    @Mock
    private UserService userService;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private EventService eventService;

    private EventRequestDTO eventRequest;
    private Event event;
    private User currentUser;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrow = now.plusDays(1);
        LocalDateTime nextWeek = now.plusDays(7);

        event = Event.builder()
                .id("event-123")
                .title("Workshop Spring Boot")
                .description("Aprenda Spring Boot")
                .categoryCode("TECH")
                .locationCode("SP-001")
                .startDate(tomorrow)
                .endDate(tomorrow.plusHours(3))
                .maxCapacity(50)
                .currentCapacity(0)
                .price(99.99)
                .organizerId("organizer@email.com")
                .status(Event.EventStatus.DRAFT)
                .tags(Arrays.asList("java", "spring"))
                .attendees(new HashSet<>())
                .createdAt(now)
                .updatedAt(now)
                .build();

        eventRequest = EventRequestDTO.builder()
                .title("Workshop Spring Boot")
                .description("Aprenda Spring Boot")
                .categoryCode("TECH")
                .locationCode("SP-001")
                .startDate(tomorrow)
                .endDate(tomorrow.plusHours(3))
                .maxCapacity(50)
                .price(99.99)
                .tags(Arrays.asList("java", "spring"))
                .build();

        currentUser = User.builder()
                .id("user-123")
                .email("organizer@email.com")
                .role(User.UserRole.ORGANIZER)
                .build();

        Category category = Category.builder()
                .id("TECH")
                .name("Tecnologia")
                .description("Eventos de tecnologia")
                .build();

        Location location = Location.builder()
                .id("SP-001")
                .name("Centro de Eventos SP")
                .city("São Paulo")
                .state("SP")
                .country("Brasil")
                .build();
    }

    @Test
    @DisplayName("Should create event successfully")
    void shouldCreateEventSuccessfully() {
        doNothing().when(permissionAuthorizationService).validatePermission(Permission.EVENT_CREATE);
        doNothing().when(userService).addOrganizedEvent(anyString(), anyString());
        when(permissionAuthorizationService.getCurrentUser()).thenReturn(currentUser);
        when(eventRepository.save(any(Event.class))).thenReturn(event);
        when(userService.findByEmail(anyString())).thenReturn(currentUser);

        Event result = eventService.createEvent(eventRequest);

        assertNotNull(result);
        assertEquals("event-123", result.getId());
        assertEquals("Workshop Spring Boot", result.getTitle());

        verify(permissionAuthorizationService).validatePermission(Permission.EVENT_CREATE);
        verify(eventRepository).save(any(Event.class));
        verify(userService).addOrganizedEvent(currentUser.getId(), event.getId());
        verify(auditService).logChange(anyString(), anyString(), anyString(), any(), any(), any(), anyString());
    }

    @Test
    @DisplayName("Should throw exception if event not found")
    void shouldThrowWhenEventNotFound() {
        when(eventRepository.findById("not-found")).thenReturn(Optional.empty());
        when(messageService.getMessage(anyString(), any())).thenReturn("Event not found");

        BusinessException exception = assertThrows(BusinessException.class,
                () -> eventService.findEventDTOById("not-found"));

        assertEquals("Event not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should return all events with DTOs")
    void shouldReturnAllEventDTOs() {
        when(eventRepository.findAll()).thenReturn(List.of(event));
        when(categoryRepository.findByCategoryCode(anyString())).thenReturn(Optional.empty());
        when(locationRepository.findByLocationCode(anyString())).thenReturn(Optional.empty());

        List<EventDTO> result = eventService.findAllEventDTOs();

        assertEquals(1, result.size());
        assertEquals(event.getId(), result.get(0).getId());
    }

    @Test
    @DisplayName("Should update event successfully")
    void shouldUpdateEventSuccessfully() {
        EventRequestDTO updatedRequest = EventRequestDTO.builder()
                .title("Updated Title")
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .build();

        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        doNothing().when(permissionAuthorizationService).validateEventModification(event);
        when(eventRepository.save(any(Event.class))).thenReturn(event);
        when(permissionAuthorizationService.getCurrentUser()).thenReturn(currentUser);

        Event updatedEvent = eventService.updateEvent(event.getId(), updatedRequest);

        assertNotNull(updatedEvent);
        assertEquals(event.getId(), updatedEvent.getId());
    }

    @Test
    @DisplayName("Should delete event successfully")
    void shouldDeleteEventSuccessfully() {
        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        doNothing().when(permissionAuthorizationService).validatePermission(Permission.EVENT_DELETE);
        when(permissionAuthorizationService.getCurrentUser()).thenReturn(currentUser);
        when(userService.findByEmail(event.getOrganizerId())).thenReturn(currentUser);
        doNothing().when(userService).removeOrganizedEvent(currentUser.getId(), event.getId());
        doNothing().when(auditService).logChange(anyString(), anyString(), anyString(), any(), any(), any(), anyString());

        assertDoesNotThrow(() -> eventService.deleteEvent(event.getId()));

        verify(eventRepository).deleteById(event.getId());
    }

    @Test
    @DisplayName("Should publish event successfully")
    void shouldPublishEventSuccessfully() {
        event.setStatus(Event.EventStatus.DRAFT);

        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        doNothing().when(permissionAuthorizationService).validateEventModification(event);
        doNothing().when(publishingValidationService).validateForPublishing(event);
        when(eventRepository.save(event)).thenReturn(event);

        Event published = eventService.publishEvent(event.getId());

        assertEquals(Event.EventStatus.PUBLISHED, published.getStatus());
        verify(eventRepository).save(event);
    }

    @Test
    @DisplayName("Should add attendee successfully")
    void shouldAddAttendeeSuccessfully() {
        String userId = "user-123";

        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        doNothing().when(permissionAuthorizationService).validateEventModification(event);
        when(attendeeValidationChainService.validate(event, userId)).thenReturn(null);
        when(eventRepository.save(event)).thenReturn(event);

        var result = eventService.addAttendee(event.getId(), userId);

        assertTrue(result.isSuccess());
        assertEquals(userId, result.getData().getUserId());
        assertTrue(event.getAttendees().contains(userId));
    }

    @Test
    @DisplayName("Should cancel event successfully")
    void shouldCancelEventSuccessfully() {
        event.setStatus(Event.EventStatus.PUBLISHED);

        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        when(eventRepository.save(event)).thenReturn(event);

        assertDoesNotThrow(() -> eventService.cancelEvent(event.getId()));

        assertEquals(Event.EventStatus.CANCELLED, event.getStatus());
    }
}
package br.com.eventmanager.application.service;

import br.com.eventmanager.adapter.inbound.rest.exception.BusinessException;
import br.com.eventmanager.adapter.outbound.persistence.CategoryRepository;
import br.com.eventmanager.adapter.outbound.persistence.EventRepository;
import br.com.eventmanager.adapter.outbound.persistence.LocationRepository;
import br.com.eventmanager.domain.Category;
import br.com.eventmanager.domain.Event;
import br.com.eventmanager.domain.Location;
import br.com.eventmanager.domain.User;
import br.com.eventmanager.domain.dto.*;
import br.com.eventmanager.domain.mapper.EventMapper;
import br.com.eventmanager.domain.security.Permission;
import br.com.eventmanager.shared.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static br.com.eventmanager.shared.Constants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {
    
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final EventMapper eventMapper;
    private final MessageService messageService;
    private final PublishingValidationService publishingValidationService;
    private final AttendeeValidationChainService attendeeValidationChainService;
    private final PermissionAuthorizationService permissionAuthorizationService;
    private final UserService userService;

    public Event createEvent(EventRequestDTO eventRequest) {

        permissionAuthorizationService.validatePermission(Permission.EVENT_CREATE);

        validateEventCreation(eventRequest);

        String currentUserEmail = permissionAuthorizationService.getCurrentUser().getEmail();
        eventRequest.setOrganizerId(currentUserEmail);
        eventRequest.setCreatedAt(LocalDateTime.now());
        eventRequest.setUpdatedAt(LocalDateTime.now());
        eventRequest.setStatus(Event.EventStatus.DRAFT);
        eventRequest.setCurrentCapacity(0);

        Event event = new Event();
        eventMapper.toEvent(eventRequest, event);
        var savedEvent = eventRepository.save(event);
        User currentUser = userService.findByEmail(currentUserEmail);
        userService.addOrganizedEvent(currentUser.getId(), savedEvent.getId());
        return savedEvent;
    }

    public Optional<EventDTO> findEventDTOById(String id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new BusinessException(messageService.getMessage(EM_0008, id)));

        permissionAuthorizationService.validateEventVisibility(event);

        Category category = categoryRepository.findByCategoryCode(event.getCategoryCode()).orElse(null);
        Location location = locationRepository.findByLocationCode(event.getLocationCode()).orElse(null);

        return Optional.of(buildEventDTO(event, category, location));
    }

    public List<EventDTO> findAllEventDTOs() {
        List<Event> events = eventRepository.findAll();

        return events.stream()
                .filter(this::filterByPermission)
                .map(event -> {
                    Category category = categoryRepository.findByCategoryCode(event.getCategoryCode()).orElse(null);
                    Location location = locationRepository.findByLocationCode(event.getLocationCode()).orElse(null);
                    return buildEventDTO(event, category, location);
                })
                .collect(Collectors.toList());
    }
    
    public List<EventDTO> findEventDTOsByCategory(String category) {
        return eventRepository.findByCategoryCode(category).stream()
                .filter(this::filterByPermission)
                .map(event -> {
                    Category categoryEntity = categoryRepository.findByCategoryCode(event.getCategoryCode()).orElse(null);
                    Location location = locationRepository.findByLocationCode(event.getLocationCode()).orElse(null);
                    return buildEventDTO(event, categoryEntity, location);
                })
                .collect(Collectors.toList());
    }
    
    public List<EventDTO> findEventDTOsByStatus(Event.EventStatus status) {
        return eventRepository.findByStatus(status).stream()
                .filter(this::filterByPermission)
                .map(event -> {
                    Category category = categoryRepository.findById(event.getCategoryCode()).orElse(null);
                    return buildEventDTO(event, category, null);
                })
                .collect(Collectors.toList());
    }

    private boolean filterByPermission(Event event) {
        try {
            permissionAuthorizationService.validateEventVisibility(event);
            return true;
        } catch (BusinessException e) {
            return false;
        }
    }

    public Event updateEvent(String id, EventRequestDTO eventDetails) {
        Event existingEvent = eventRepository.findById(id)
                .orElseThrow(() -> new BusinessException(messageService.getMessage(EM_0008, id)));

        permissionAuthorizationService.validateEventModification(existingEvent);

        validateEventUpdate(existingEvent, eventDetails);
        eventMapper.toEvent(eventDetails, existingEvent);
        validateEventAfterUpdate(existingEvent);

        existingEvent.setUpdatedAt(LocalDateTime.now());

        return eventRepository.save(existingEvent);
    }

    public void deleteEvent(String id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new BusinessException(messageService.getMessage("EM-0008", id)));

        permissionAuthorizationService.validatePermission(Permission.EVENT_DELETE);

        if (event.getOrganizerId() != null) {

            User organizer = userService.findByEmail(event.getOrganizerId());
            userService.removeOrganizedEvent(organizer.getId(), id);

            eventRepository.deleteById(id);
        }
    }

    public Event publishEvent(String id) {
        return eventRepository.findById(id)
                .map(event -> {
                    if (event.getStatus() != Event.EventStatus.DRAFT) {
                        throw new BusinessException(messageService.getMessage(Constants.EM_0012));
                    }

                    permissionAuthorizationService.validateEventModification(event);
                    publishingValidationService.validateForPublishing(event);

                    validateEventForPublishing(event);

                    event.setStatus(Event.EventStatus.PUBLISHED);
                    event.setUpdatedAt(LocalDateTime.now());

                    log.info("Publishing event: {}", id);
                    return eventRepository.save(event);
                })
                .orElseThrow(() -> new BusinessException(messageService.getMessage(Constants.EM_0008, id)));
    }

    public Event cancelEvent(String id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new BusinessException(messageService.getMessage("EM-0008", id)));

        permissionAuthorizationService.validateEventModification(event);

        if (event.getStatus() != Event.EventStatus.PUBLISHED) {
            throw new BusinessException(messageService.getMessage("EM-0013"));
        }

        event.setStatus(Event.EventStatus.CANCELLED);
        event.setUpdatedAt(LocalDateTime.now());

        return eventRepository.save(event);
    }

    @Scheduled(fixedRate = 3600000)
    public void completeExpiredEvents() {
        LocalDateTime now = LocalDateTime.now();

        List<Event> publishedEvents = eventRepository.findByStatus(Event.EventStatus.PUBLISHED);

        publishedEvents.stream()
                .filter(event -> event.getEndDate().isBefore(now))
                .forEach(event -> {
                    event.setStatus(Event.EventStatus.COMPLETED);
                    event.setUpdatedAt(now);
                    eventRepository.save(event);
                    log.info("Event {} marked as completed", event.getId());
                });
    }

    private void validateEventForPublishing(Event event) {
        validateEventDates(event.getStartDate(), event.getEndDate());
    }

    public ResultDTO<AttendeeResponseDTO> addAttendee(String eventId, String userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new BusinessException(messageService.getMessage(EM_0008, eventId)));

        permissionAuthorizationService.validateEventModification(event);

        AttendeeResponseDTO validationError = attendeeValidationChainService.validate(event, userId);
        if (validationError != null) {
            return ResultDTO.failure(validationError.getMessage());
        }

        addAttendee(userId, event);
        event.setCurrentCapacity(event.getCurrentCapacity() + 1);
        event.setUpdatedAt(LocalDateTime.now());

        eventRepository.save(event);

        log.info("Added attendee {} to event {}", userId, eventId);

        AttendeeResponseDTO successResponse = buildAttendeeResponse(event, userId, true,
                "Usuário inscrito com sucesso no evento!");

        return ResultDTO.success(successResponse);
    }

    private AttendeeResponseDTO buildAttendeeResponse(Event event, String userId, boolean success, String message) {
        return AttendeeResponseDTO.builder()
                .eventId(event.getId())
                .eventTitle(event.getTitle())
                .userId(userId)
                .success(success)
                .message(message)
                .registeredAt(success ? LocalDateTime.now() : null)
                .currentCapacity(event.getCurrentCapacity())
                .maxCapacity(event.getMaxCapacity())
                .remainingSpots(event.getMaxCapacity() - event.getCurrentCapacity())
                .build();
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
                .category(categoryDTO)
                .location(locationDTO)
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

    private void validateEventCreation(EventRequestDTO event) {
        validateEventDates(event.getStartDate(), event.getEndDate());
        validateEventTags(event.getTags());
        initiateLists(event);
    }

    private static void initiateLists(EventRequestDTO event) {
        if (event.getTags() == null) {
            event.setTags(new ArrayList<>());
        }
        if (event.getAttendees() == null) {
            event.setAttendees(new HashSet<>());
        }
    }

    private void validateEventDates(LocalDateTime startDate, LocalDateTime endDate) {
        LocalDateTime now = LocalDateTime.now();

        if (startDate.isBefore(now)) {
            throw new BusinessException(messageService.getMessage(EM_0001));
        }

        if (endDate.isBefore(startDate)) {
            throw new BusinessException(messageService.getMessage(EM_0002));
        }

        Duration duration = Duration.between(startDate, endDate);
        if (duration.toHours() < 1) {
            throw new BusinessException(messageService.getMessage(EM_0003));
        }

        if (duration.toDays() > 7) {
            throw new BusinessException(messageService.getMessage(EM_0004));
        }
    }

    private void validateEventTags(List<String> tags) {
        if (tags != null) {
            if (tags.size() > 10) {
                throw new BusinessException(messageService.getMessage(EM_0005));
            }
        }
    }

    private void validateEventUpdate(Event existingEvent, EventRequestDTO eventDetails) {
        if (existingEvent.getStatus() == Event.EventStatus.PUBLISHED) {
            validatePublishedEventUpdate(eventDetails);
        }

        if (existingEvent.getStatus() == Event.EventStatus.CANCELLED ||
                existingEvent.getStatus() == Event.EventStatus.COMPLETED) {
            throw new BusinessException(messageService.getMessage(EM_0007, existingEvent.getStatus().name()));
        }
    }

    private void validatePublishedEventUpdate(EventRequestDTO eventDetails) {
        if (eventDetails.getStartDate() != null && eventDetails.getStartDate().isBefore(LocalDateTime.now())) {
            throw new BusinessException(messageService.getMessage(EM_0006));
        }
    }

    private void validateEventAfterUpdate(Event event) {
        if (event.getStartDate() != null && event.getEndDate() != null) {
            validateEventDates(event.getStartDate(), event.getEndDate());
        }

        validateEventTags(event.getTags());
    }
}

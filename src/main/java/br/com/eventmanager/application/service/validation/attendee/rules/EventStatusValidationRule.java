package br.com.eventmanager.application.service.validation.attendee.rules;

import br.com.eventmanager.application.service.validation.attendee.AttendeeValidationPriority;
import br.com.eventmanager.application.service.validation.attendee.AttendeeValidationRule;
import br.com.eventmanager.domain.Event;
import br.com.eventmanager.domain.dto.AttendeeResponseDTO;
import org.springframework.stereotype.Component;

@Component
@AttendeeValidationPriority(1)
public class EventStatusValidationRule implements AttendeeValidationRule {

    @Override
    public AttendeeResponseDTO validate(Event event, String userId) {
        if (event.getStatus() != Event.EventStatus.PUBLISHED) {
            return AttendeeResponseDTO.builder()
                    .eventId(event.getId())
                    .eventTitle(event.getTitle())
                    .userId(userId)
                    .success(false)
                    .message("Evento não está publicado. Apenas eventos publicados aceitam inscrições.")
                    .currentCapacity(event.getCurrentCapacity())
                    .maxCapacity(event.getMaxCapacity())
                    .remainingSpots(event.getMaxCapacity() - event.getCurrentCapacity())
                    .build();
        }
        return null;
    }

    @Override
    public boolean canHandle(Event event, String userId) {
        return true;
    }
}

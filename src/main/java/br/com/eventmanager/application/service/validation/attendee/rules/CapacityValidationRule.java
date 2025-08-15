package br.com.eventmanager.application.service.validation.attendee.rules;

import br.com.eventmanager.application.service.validation.attendee.AttendeeValidationPriority;
import br.com.eventmanager.application.service.validation.attendee.AttendeeValidationRule;
import br.com.eventmanager.domain.Event;
import br.com.eventmanager.domain.dto.AttendeeResponseDTO;
import org.springframework.stereotype.Component;

@Component
@AttendeeValidationPriority(3)
public class CapacityValidationRule implements AttendeeValidationRule {
    @Override
    public AttendeeResponseDTO validate(Event event, String userId) {
        if (event.getCurrentCapacity() >= event.getMaxCapacity()) {
            return AttendeeResponseDTO.builder()
                    .eventId(event.getId())
                    .eventTitle(event.getTitle())
                    .userId(userId)
                    .success(false)
                    .message("Evento está com capacidade máxima atingida.")
                    .currentCapacity(event.getCurrentCapacity())
                    .maxCapacity(event.getMaxCapacity())
                    .remainingSpots(0)
                    .build();
        }
        return null;
    }

    @Override
    public boolean canHandle(Event event, String userId) {
        return event.getStatus() == Event.EventStatus.PUBLISHED;
    }
}

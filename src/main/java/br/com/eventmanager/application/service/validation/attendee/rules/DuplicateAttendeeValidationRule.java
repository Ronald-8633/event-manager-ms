package br.com.eventmanager.application.service.validation.attendee.rules;

import br.com.eventmanager.application.service.validation.attendee.AttendeeValidationPriority;
import br.com.eventmanager.application.service.validation.attendee.AttendeeValidationRule;
import br.com.eventmanager.domain.Event;
import br.com.eventmanager.domain.dto.AttendeeResponseDTO;
import org.springframework.stereotype.Component;

@Component
@AttendeeValidationPriority(2)
public class DuplicateAttendeeValidationRule implements AttendeeValidationRule {

    @Override
    public AttendeeResponseDTO validate(Event event, String userId) {
        if (event.getAttendees() != null && event.getAttendees().contains(userId)) {
            return AttendeeResponseDTO.builder()
                    .eventId(event.getId())
                    .eventTitle(event.getTitle())
                    .userId(userId)
                    .success(false)
                    .message("Usuário já está inscrito neste evento.")
                    .currentCapacity(event.getCurrentCapacity())
                    .maxCapacity(event.getMaxCapacity())
                    .remainingSpots(event.getMaxCapacity() - event.getCurrentCapacity())
                    .build();
        }
        return null;
    }

    @Override
    public boolean canHandle(Event event, String userId) {
        return event.getStatus() == Event.EventStatus.PUBLISHED
                && event.getCurrentCapacity() < event.getMaxCapacity();
    }
}

package br.com.eventmanager.application.service.validation.attendee.rules;

import br.com.eventmanager.application.service.validation.attendee.AttendeeValidationPriority;
import br.com.eventmanager.application.service.validation.attendee.AttendeeValidationRule;
import br.com.eventmanager.domain.Event;
import br.com.eventmanager.domain.dto.AttendeeResponseDTO;

import java.time.LocalDateTime;
@AttendeeValidationPriority(4)
public class RegistrationDeadlineValidationRule implements AttendeeValidationRule {
    @Override
    public AttendeeResponseDTO validate(Event event, String userId) {
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime registrationDeadline = event.getStartDate().minusHours(1);

        if (now.isAfter(registrationDeadline)) {
            return AttendeeResponseDTO.builder()
                    .eventId(event.getId())
                    .eventTitle(event.getTitle())
                    .userId(userId)
                    .success(false)
                    .message("Inscrições encerraram. O evento começa em menos de 1 hora.")
                    .currentCapacity(event.getCurrentCapacity())
                    .maxCapacity(event.getMaxCapacity())
                    .remainingSpots(event.getMaxCapacity() - event.getCurrentCapacity())
                    .build();
        }

        return null;
    }

    @Override
    public boolean canHandle(Event event, String userId) {
        return event.getCurrentCapacity() < event.getMaxCapacity();
    }
}

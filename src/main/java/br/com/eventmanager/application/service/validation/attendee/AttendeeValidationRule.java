package br.com.eventmanager.application.service.validation.attendee;

import br.com.eventmanager.domain.Event;
import br.com.eventmanager.domain.dto.AttendeeResponseDTO;

public interface AttendeeValidationRule {

    AttendeeResponseDTO validate(Event event, String userId);

    boolean canHandle(Event event, String userId);
}

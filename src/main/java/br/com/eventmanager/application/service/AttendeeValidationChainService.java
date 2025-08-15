package br.com.eventmanager.application.service;

import br.com.eventmanager.application.service.validation.attendee.AttendeeValidationPriority;
import br.com.eventmanager.application.service.validation.attendee.AttendeeValidationRule;
import br.com.eventmanager.domain.Event;
import br.com.eventmanager.domain.dto.AttendeeResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AttendeeValidationChainService {
    private final List<AttendeeValidationRule> validationRules;

    public AttendeeResponseDTO validate(Event event, String userId) {
        return validationRules.stream()
                .sorted(Comparator.comparingInt(rule -> {
                    AttendeeValidationPriority priority = rule.getClass().getAnnotation(AttendeeValidationPriority.class);
                    return priority != null ? priority.value() : 999;
                }))
                .filter(rule -> rule.canHandle(event, userId))
                .map(rule -> rule.validate(event, userId))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }
}

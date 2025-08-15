package br.com.eventmanager.application.service;

import br.com.eventmanager.application.service.validation.publish.PublishingValidationRule;
import br.com.eventmanager.domain.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PublishingValidationService {

    private final List<PublishingValidationRule> validationRules;

    public void validateForPublishing(Event event) {
        log.debug("Validating event {} for publishing", event.getId());

        validationRules.forEach(rule -> rule.validate(event));

        log.debug("Event {} passed all publishing validations", event.getId());
    }
}

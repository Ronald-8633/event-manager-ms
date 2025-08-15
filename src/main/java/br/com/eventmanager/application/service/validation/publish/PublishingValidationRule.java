package br.com.eventmanager.application.service.validation.publish;

import br.com.eventmanager.domain.Event;

public interface PublishingValidationRule {

    String validate(Event event);
}

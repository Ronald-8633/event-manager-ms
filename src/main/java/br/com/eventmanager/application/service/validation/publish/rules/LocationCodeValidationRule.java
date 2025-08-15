package br.com.eventmanager.application.service.validation.publish.rules;

import br.com.eventmanager.adapter.inbound.rest.exception.BusinessException;
import br.com.eventmanager.adapter.outbound.persistence.LocationRepository;
import br.com.eventmanager.application.service.MessageService;
import br.com.eventmanager.application.service.validation.publish.PublishingValidationRule;
import br.com.eventmanager.domain.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static br.com.eventmanager.shared.Constants.EM_0010;
import static br.com.eventmanager.shared.Constants.EM_0011;

@Component
@RequiredArgsConstructor
public class LocationCodeValidationRule implements PublishingValidationRule {

    private final LocationRepository locationRepository;
    private final MessageService messageService;

    @Override
    public String validate(Event event) {
        if (event.getLocationCode() == null || event.getLocationCode().trim().isEmpty()) {
            throw new BusinessException(messageService.getMessage(EM_0011, event.getLocationCode()));
        }

        if (!locationRepository.existsByLocationCode(event.getLocationCode())) {
            throw new BusinessException(messageService.getMessage(EM_0010, event.getLocationCode()));
        }

        return null;
    }
}

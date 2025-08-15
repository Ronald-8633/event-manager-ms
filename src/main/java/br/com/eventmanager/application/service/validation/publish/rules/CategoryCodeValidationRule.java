package br.com.eventmanager.application.service.validation.publish.rules;

import br.com.eventmanager.adapter.inbound.rest.exception.BusinessException;
import br.com.eventmanager.adapter.outbound.persistence.CategoryRepository;
import br.com.eventmanager.application.service.MessageService;
import br.com.eventmanager.application.service.validation.publish.PublishingValidationRule;
import br.com.eventmanager.domain.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static br.com.eventmanager.shared.Constants.EM_0009;
import static br.com.eventmanager.shared.Constants.EM_0011;

@Component
@RequiredArgsConstructor
public class CategoryCodeValidationRule implements PublishingValidationRule {

    private final CategoryRepository categoryRepository;
    private final MessageService messageService;

    @Override
    public String validate(Event event) {
        if (event.getCategoryCode() == null || event.getCategoryCode().trim().isEmpty()) {
            throw new BusinessException(messageService.getMessage(EM_0011, event.getCategoryCode()));
        }

        if (!categoryRepository.existsByCategoryCode(event.getCategoryCode())) {
            throw new BusinessException(messageService.getMessage(EM_0009, event.getCategoryCode()));
        }

        return null;
    }
}

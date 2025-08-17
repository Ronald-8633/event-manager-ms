package br.com.eventmanager.application.service;

import br.com.eventmanager.adapter.inbound.rest.exception.BusinessException;
import br.com.eventmanager.application.service.validation.publish.PublishingValidationRule;
import br.com.eventmanager.domain.Event;
import br.com.eventmanager.domain.Event.EventStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PublishingValidationServiceTest {

    @Mock
    private PublishingValidationRule rule1;

    @Mock
    private PublishingValidationRule rule2;

    private PublishingValidationService publishingValidationService;

    private Event event;

    @BeforeEach
    void setup() {
        publishingValidationService = new PublishingValidationService(List.of(rule1, rule2));
        event = Event.builder()
                .id("show")
                .title("Show de rock")
                .description("Apresentação de rock")
                .categoryCode("CAT01")
                .locationCode("LOC01")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(1))
                .maxCapacity(100)
                .price(10.0)
                .status(EventStatus.DRAFT)
                .build();
    }

    @Test
    void givenValidEvent_whenValidateForPublishing_thenAllRulesAreExecuted() {
        publishingValidationService.validateForPublishing(event);

        verify(rule1).validate(event);
        verify(rule2).validate(event);
    }

    @Test
    void givenInvalidEvent_whenAnyRuleFails_thenThrowsBusinessException() {
        doThrow(new BusinessException("Invalid event")).when(rule1).validate(event);

        assertThatThrownBy(() -> publishingValidationService.validateForPublishing(event))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Invalid event");

        verify(rule1).validate(event);
        verify(rule2, never()).validate(event);
    }
}
